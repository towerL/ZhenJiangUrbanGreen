package com.nju.urbangreen.zhenjiangurbangreen.attachments;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.util.Log;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.notification.BaseNotificationItem;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationHelper;
import com.liulishuo.filedownloader.notification.FileDownloadNotificationListener;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.util.FileUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.SPUtils;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import net.gotev.uploadservice.BinaryUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by lxs on 17-8-18.
 */

public class AttachmentService {

    private static FileDownloadNotificationHelper<DownloadNotificationItem> downloadNoticeHelper
            = new FileDownloadNotificationHelper<>();

    public static void viewAttach(Context context, final AttachmentRecord attach, Callback cb) {
        File file = new File(attach.localPath);
        if(!file.exists()) {
            cb.failed("文件不存在");
            return;
        }
        Intent viewIntent = FileUtil.getFileViewIntent(attach.localPath);
        if(viewIntent != null) {
            try {
                if(FileUtil.isIntentAvailable(viewIntent))
                    context.startActivity(viewIntent);
                else {
                    cb.success();
                    Toast.makeText(context, "本机不支持该类文件的查看", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                cb.failed(e.getMessage());
            }
        } else {
            cb.success();
            Toast.makeText(context, "本机不支持该类文件的查看", Toast.LENGTH_SHORT).show();
        }
    }

    public static void renameAttach(Context context, final AttachmentRecord attach,
                                    final AttachmentService.Callback cb) {
        final String extension = attach.fileName.substring(attach.fileName.lastIndexOf("."),attach.fileName.length());
        new MaterialDialog.Builder(context)
                .title("重命名文件")
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input("文件名", attach.fileName.substring(0,attach.fileName.lastIndexOf(".")), new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                        attach.fileName = input.toString() + extension;
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        cb.success();
                    }
                })
                .show();
    }

    public static void removeAttach(Context context, final AttachmentRecord attach,
                                    final Callback cb) {
        final ProgressDialog loading = new ProgressDialog(context);
        loading.setMessage("删除附件中，请稍候...");
        loading.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg[] = new String[1];
                boolean res = WebServiceUtils.removeAttachment(attach.fileID, errorMsg);
                loading.dismiss();
                Looper.prepare();
                if(res) {
                    cb.success();
                } else {
                    cb.failed("删除失败");
                }
                Looper.loop();
            }
        }).start();
    }

    public static void uploadAttach(Context context, String parentID, final AttachmentRecord attach,
                                    final AttachmentService.Callback cb) {
        try {
            final String uploadId = UUID.randomUUID().toString();
            HashMap<String, Object> params = new HashMap<>();
            params.put("ParentID", parentID);
            params.put("FileName", attach.fileName);
            params.put("FAID", attach.fileID);
            params.put("FileSize", attach.fileSize);
            String serverUrl = WebServiceUtils.getFileUploadUrl(params);
            Log.i("Upload", serverUrl);

            BinaryUploadRequest request = new BinaryUploadRequest(context, uploadId, serverUrl)
                    .setFileToUpload(attach.localPath)
                    .setMethod("POST")
                    .setNotificationConfig(getUploadNotificationConfig(context, attach.fileName))
                    .setMaxRetries(SPUtils.getInt("MAX_RETRIES", 2))
                    .setDelegate(new UploadStatusDelegate() {
                        @Override
                        public void onProgress(Context context, UploadInfo uploadInfo) {
                            cb.progress(uploadInfo.getProgressPercent(), uploadInfo.getUploadRateString());
                        }
                        @Override
                        public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {
                            cb.failed("上传失败");
                        }
                        @Override
                        public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {
                            cb.success();
                        }
                        @Override
                        public void onCancelled(Context context, UploadInfo uploadInfo) {
                            cb.failed("上传失败");
                        }
                    });
            request.startUpload();

        } catch (Exception exc) {
            Toast.makeText(context, exc.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void downloadAttach(final Context context, final AttachmentRecord attach,
                                      final AttachmentService.Callback cb) {
        Map<String, Object> params = new HashMap<>();
        params.put("FAID", attach.fileID);
        String url = WebServiceUtils.getFileDownloadUrl(params);
        final String savePath = FileUtil.getAttachSaveDir() + attach.parentID + File.separator + attach.fileName;
        Log.i("Download", url);
        FileDownloader.getImpl().create(url)
                .setPath(savePath)
                .setListener(new FileDownloadNotificationListener(downloadNoticeHelper) {
                    @Override
                    protected BaseNotificationItem create(BaseDownloadTask task) {
                        return new DownloadNotificationItem(context, task.getId(), attach.fileName, "附件下载");
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        int progress = (int)((float)soFarBytes / totalBytes * 100.f);
                        cb.progress(progress, task.getSpeed() + "KB/s");
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        attach.localPath = savePath;
                        cb.success();
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        cb.failed(e.getMessage());
                    }
                }).start();
    }


    private static UploadNotificationConfig getUploadNotificationConfig(Context context, String filename) {
        UploadNotificationConfig config = new UploadNotificationConfig();

        PendingIntent clickIntent = PendingIntent.getActivity(context, 1,
                new Intent(context, AttachmentListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        config.setTitleForAllStatuses(filename)
                .setRingToneEnabled(true)
                .setClickIntentForAllStatuses(clickIntent)
                .setClearOnActionForAllStatuses(true);

        config.getProgress().message = context.getResources().getString(R.string.uploading);
        config.getProgress().iconColorResourceID = context.getResources().getColor(R.color.colorPrimary);

        config.getCompleted().message = context.getResources().getString(R.string.upload_success);
        config.getCompleted().iconColorResourceID = context.getResources().getColor(R.color.green_land_border);

        config.getError().message = context.getResources().getString(R.string.upload_error);
        config.getError().iconColorResourceID = context.getResources().getColor(R.color.colorAccent);

        return config;
    }

    public interface Callback {
        void success();
        void failed(String msg);
        void progress(int progress, String speed);
    }

}
