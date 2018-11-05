package com.nju.urbangreen.zhenjiangurbangreen.settings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.startup.LoginActivity;
import com.nju.urbangreen.zhenjiangurbangreen.util.ActivityCollector;
import com.nju.urbangreen.zhenjiangurbangreen.util.CacheUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.DownloadNewApkService;
import com.nju.urbangreen.zhenjiangurbangreen.util.SPUtils;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;
import com.nju.urbangreen.zhenjiangurbangreen.widget.TitleBarLayout;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    private ProgressDialog progressDialog;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ((AlertDialog.Builder) msg.obj).show();
        }
    };

    @BindView(R.id.et_settings_URL)
    public EditText etURL;

    @BindView(R.id.et_settings_radius)
    public EditText etRaduis;

    @BindView(R.id.btn_save_setting)
    public Button btnSave;

    @BindView(R.id.btn_check_update)
    public Button btnHandUpdate;

    @BindView(R.id.btn_clear_cache)
    public Button btnClearCache;

    @BindView(R.id.ly_settings_title_bar)
    public TitleBarLayout titleBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setURL();
        setTitleBar();
        setSettingValue();
        setSaveButton();
        setHandUpdateButton();
        setClearButton();

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    private void setSaveButton() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    SPUtils.put("NearRadius", Float.parseFloat(etRaduis.getText().toString()));
                } catch(Exception e) {
                    showFormatErrorToast("行道树缓冲区半径");
                    return;
                }
                try {
                    WebServiceUtils.putServerAddress("http://" + etURL.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(SettingsActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setURL() {
        etURL.setText(WebServiceUtils.getServerAddress().replace("http://",""));
    }

    private void showFormatErrorToast(String key) {
        Toast.makeText(SettingsActivity.this, key + "格式有误", Toast.LENGTH_SHORT).show();
    }

    private void setSettingValue() {
        etRaduis.setText(SPUtils.getFloat("NearRadius", 50.f).toString());
    }

    private void setClearButton() {
        btnClearCache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("提示");
                builder.setMessage("确定要清除缓存吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CacheUtil.removeUGOS();
                        CacheUtil.removeUGOSug();
                        dialogInterface.dismiss();
                        Toast.makeText(SettingsActivity.this, "清除缓存成功", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    private void setHandUpdateButton(){
        btnHandUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(SettingsActivity.this);
                progressDialog.setMessage("正在检查更新...");
                progressDialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String[] errMsg = new String[1];
                        Map<String, Object> resultsUpdate = WebServiceUtils.checkUpdate(errMsg);
                        progressDialog.dismiss();

                        if (resultsUpdate != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                            builder.setTitle("更新提示");
                            builder.setMessage("检测到软件有更新，是否现在安装?");
                            builder.setCancelable(false);
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent1 = new Intent(SettingsActivity.this, DownloadNewApkService.class);
                                    startService(intent1);

                                }
                            });
                            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            try {
                                Thread.sleep(800);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Message message = new Message();
                            message.obj = builder;
                            handler.sendMessage(message);
                        } else if(errMsg[0] != null) {
                            Looper.prepare();
                            Toast.makeText(SettingsActivity.this, errMsg[0], Toast.LENGTH_LONG).show();
                            Looper.loop();
                        }
                    }
                }).start();
            }
        });
    }

    private void setTitleBar(){
        titleBarLayout.setTitleText("设置");
        titleBarLayout.setBtnBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleBarLayout.setBtnSelfDefBkg(R.drawable.ic_logout);
        titleBarLayout.setBtnSelfDefClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("退出提示");
                builder.setMessage("确定要退出登录吗?");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCollector.finishAll();
                        SPUtils.remove("username");
                        SPUtils.remove("password");
                        SPUtils.put("RememberPassword",false);
                        Intent intent1 = new Intent(SettingsActivity.this,LoginActivity.class);
                        startActivity(intent1);
//                        finish();

                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });
    }

    public int getVersion() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return info.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
}
