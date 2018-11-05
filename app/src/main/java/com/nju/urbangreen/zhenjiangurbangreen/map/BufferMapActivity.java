package com.nju.urbangreen.zhenjiangurbangreen.map;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.LocationDisplayManager;
import com.esri.android.map.MapView;
import com.esri.android.map.ags.ArcGISLocalTiledLayer;
import com.esri.android.map.event.OnPanListener;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;
import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.PictureMarkerSymbol;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.Symbol;
import com.nju.urbangreen.zhenjiangurbangreen.R;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.BaseActivity;
import com.nju.urbangreen.zhenjiangurbangreen.basisClass.GreenObject;
import com.nju.urbangreen.zhenjiangurbangreen.util.FileUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.GeoJsonUtil;
import com.nju.urbangreen.zhenjiangurbangreen.util.WebServiceUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by lxs on 17-10-27.
 */

public class BufferMapActivity  extends BaseActivity{

    private ArcGISLocalTiledLayer localTPKLayer;
    private Envelope fullExtent = new Envelope(491067.0,3558797.0,501540.0,3566679.0);
    private LocationManager locationManager;
    private LocationDisplayManager locationDisplayManager;
    private GraphicsLayer streetTreeLayer;
    private GraphicsLayer ancientTreeLayer;
    private GraphicsLayer greenLandLayer;
    private GraphicsLayer bufferCenterPinLayer;
    private GraphicsLayer bufferLayer;

    private static final String GreenLandType = "000", AncientTreeType = "001", StreetTreeType = "002";
    private static Map<String, Symbol> symbolMap;
    private ArrayList<GreenObject> searchGreenLand = new ArrayList<>(),
            searchAncientTree = new ArrayList<>(), searchStreetTree = new ArrayList<>();
    private ArrayList<GreenObject> selectedUGOs = new ArrayList<>();

    private ProgressDialog loadingDialog;
    @BindView(R.id.map_buffer)
    public MapView map;

    @BindView(R.id.imgbtn_map_buffer_locate)
    public ImageButton btnLocate;

    @BindView(R.id.et_map_buffer)
    public EditText etBuffer;

    @BindView(R.id.btn_map_buffer_search)
    public AppCompatButton btnBufferSearch;

    @BindView(R.id.Toolbar_simple)
    public Toolbar toolbar;

    @BindView(R.id.iv_map_buffer_pin)
    public ImageView ivPin;
    private int pinGraphicID;
    private int pinLastX, pinLastY;
    private int moveWidth, moveHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer_map);
        ButterKnife.bind(this);
        loadingDialog = new ProgressDialog(BufferMapActivity.this);
        initToolbar();
        setMap();
        createBufferCenterLayer();
        setPinImage();
        btnBufferSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point bufferPin = (Point) bufferCenterPinLayer.getGraphic(pinGraphicID).getGeometry();
                double x = bufferPin.getX(), y = bufferPin.getY(), radius = Double.valueOf(etBuffer.getText().toString());
                getNearUGO(x, y, radius);
                drawNearBuffer(x, y, radius);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        });
        openGPS();
        symbolMap = new HashMap<>();
        SimpleFillSymbol greenLandSymbol = new SimpleFillSymbol(
                ResourcesCompat.getColor(getResources(), R.color.green_land, null), SimpleFillSymbol.STYLE.SOLID);
        greenLandSymbol.setOutline(new SimpleLineSymbol(
                ResourcesCompat.getColor(getResources(), R.color.green_land_border, null), 1.0f, SimpleLineSymbol.STYLE.SOLID));
        symbolMap.put(GreenLandType, greenLandSymbol);
        PictureMarkerSymbol ancientTreeSymbol = new PictureMarkerSymbol(new BitmapDrawable(
                BitmapFactory.decodeResource(getResources(), R.drawable.ancient_tree)));
        ancientTreeSymbol.setOffsetY(16);
        symbolMap.put(AncientTreeType, ancientTreeSymbol);
        symbolMap.put(StreetTreeType, new SimpleMarkerSymbol(
                ResourcesCompat.getColor(getResources(), R.color.colorPrimaryDark, null), 5, SimpleMarkerSymbol.STYLE.CIRCLE));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_buffer_map, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(searchAncientTree.isEmpty() && searchGreenLand.isEmpty() && searchStreetTree.isEmpty()) {
            toolbar.setTitle("图上选择绿化对象");
            menu.findItem(R.id.menu_toolbar_item_check_selected).setVisible(false);
        } else {
            int totalCount = searchAncientTree.size() + searchGreenLand.size() + searchStreetTree.size();
            toolbar.setTitle("已选 " + selectedUGOs.size() + " / " + totalCount);
            menu.findItem(R.id.menu_toolbar_item_check_selected).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_toolbar_item_check_selected) {
            Intent intent = new Intent();
            intent.putExtra("selectUgo", selectedUGOs);
            setResult(RESULT_OK, intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        toolbar.setTitle("图上选择绿化对象");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setPinImage() {
        ivPin.setImageAlpha(0);
        ivPin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(view.getId() != R.id.iv_map_buffer_pin)
                    return true;
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        pinLastX = (int) motionEvent.getRawX();
                        pinLastY = (int) motionEvent.getRawY();
                        ivPin.setImageAlpha(255);
                        bufferCenterPinLayer.setVisible(false);
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int dx = (int) motionEvent.getRawX() - pinLastX;
                        int dy = (int) motionEvent.getRawY() - pinLastY;
                        int l = view.getLeft() + dx;
                        int b = view.getBottom() + dy;
                        int r = view.getRight() + dx;
                        int t = view.getTop() + dy;
                        // 下面判断移动是否超出屏幕
                        if (l < 0) {
                            l = 0;
                            r = l + view.getWidth();
                        }
                        if (t < 0) {
                            t = 0;
                            b = t + view.getHeight();
                        }
                        if (r > moveWidth) {
                            r = moveWidth;
                            l = r - view.getWidth();
                        }
                        if (b > moveHeight) {
                            b = moveHeight;
                            t = b - view.getHeight();
                        }
                        view.layout(l, t, r, b);
                        pinLastX = (int) motionEvent.getRawX();
                        pinLastY = (int) motionEvent.getRawY();
                        view.postInvalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        float ivCenterX = 0.5f * ivPin.getLeft() + 0.5f * ivPin.getRight();
                        float ivCenterY = 0.5f * ivPin.getTop() + 0.5f * ivPin.getBottom();
                        bufferCenterPinLayer.updateGraphic(pinGraphicID,
                                map.toMapPoint(ivCenterX, ivCenterY));
                        bufferCenterPinLayer.setVisible(true);
                        ivPin.setImageAlpha(0);
                        break;
                    }
                }
                return true;
            }
        });
    }

    private void setMap() {
        ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
        String tpkFileName = FileUtil.getBaseMapTPKDir() + WebServiceUtils.BaseMapFileNames[0];
        localTPKLayer = new ArcGISLocalTiledLayer(tpkFileName);
        bufferCenterPinLayer = new GraphicsLayer();
        bufferLayer = new GraphicsLayer();
        greenLandLayer = new GraphicsLayer();
        ancientTreeLayer = new GraphicsLayer();
        streetTreeLayer = new GraphicsLayer();
        map.addLayer(localTPKLayer);
        map.addLayer(greenLandLayer);
        map.addLayer(ancientTreeLayer);
        map.addLayer(streetTreeLayer);
        map.addLayer(bufferLayer);
        map.addLayer(bufferCenterPinLayer);
        map.setExtent(fullExtent);

        map.setOnStatusChangedListener(new OnStatusChangedListener() {
            @Override
            public void onStatusChanged(Object o, STATUS status) {
                if(status == STATUS.INITIALIZED && o == map) {
                    moveWidth = map.getWidth();
                    moveHeight = map.getHeight();
                }
            }
        });

        map.setOnPanListener(new OnPanListener() {
            @Override
            public void prePointerMove(float v, float v1, float v2, float v3) {}
            @Override
            public void postPointerMove(float v, float v1, float v2, float v3) {}
            @Override
            public void prePointerUp(float v, float v1, float v2, float v3) {}

            @Override
            public void postPointerUp(float v, float v1, float v2, float v3) {
                Point pinScreen = map.toScreenPoint((Point) bufferCenterPinLayer.getGraphic(pinGraphicID).getGeometry());
                double pinScreenX = pinScreen.getX(), pinScreenY = pinScreen.getY();
                ivPin.layout((int)pinScreenX - 32, (int)pinScreenY - 32,
                        (int)pinScreenX + 32, (int)pinScreenY + 32);
                ivPin.postInvalidate();
            }
        });

        map.setOnSingleTapListener(new OnSingleTapListener() {
            @Override
            public void onSingleTap(float v, float v1) {
                if(!map.isLoaded() || (searchStreetTree.isEmpty() && searchAncientTree.isEmpty()
                        && searchGreenLand.isEmpty())) {
                    return;
                }
                GraphicsLayer[] layers = new GraphicsLayer[]{greenLandLayer, ancientTreeLayer, streetTreeLayer};
                ArrayList<GreenObject>[] objLists = new ArrayList[]{searchGreenLand, searchAncientTree, searchStreetTree};

                Integer graphicID = null;
                int layerIndex = 0;

                //在每个图层中搜索被点击的Graphics
                for(;layerIndex < layers.length;layerIndex++){
                    GraphicsLayer graphicsLayer = layers[layerIndex];
                    if(graphicsLayer == null){
                        continue;
                    }
                    if(!graphicsLayer.isVisible()){
                        continue;
                    }
                    int[] ids = graphicsLayer.getGraphicIDs(v, v1, 25, 1);
                    if(ids != null && ids.length > 0){
                        graphicID = ids[0];
                        break;
                    }
                }
                Graphic graphic = layers[layerIndex].getGraphic(graphicID);
                int[] ids = new int[]{(int)graphic.getId()};
                if(layers[layerIndex].isGraphicSelected(ids[0])) {
                    layers[layerIndex].setSelectedGraphics(ids, false);
                    for(GreenObject obj : selectedUGOs) {
                        if(obj.UGO_ID.equals(graphic.getAttributeValue("UGO_ID"))) {
                            selectedUGOs.remove(obj);
                            break;
                        }
                    }
                } else {
                    layers[layerIndex].setSelectedGraphics(ids, true);
                    for(GreenObject obj : objLists[layerIndex]) {
                        if(obj.UGO_ID.equals(graphic.getAttributeValue("UGO_ID"))) {
                            selectedUGOs.add(obj);
                            break;
                        }
                    }
                }
                invalidateOptionsMenu();
            }
        });

        locationDisplayManager = map.getLocationDisplayManager();
        locationDisplayManager.setAccuracyCircleOn(false);
        locationDisplayManager.setShowLocation(false);
        locationDisplayManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.LOCATION);
        locationDisplayManager.setShowPings(false);
        locationDisplayManager.start();//开始定位

        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Point curP = locationDisplayManager.getPoint();
                if(curP.getX() >= fullExtent.getXMin() && curP.getX() <= fullExtent.getXMax()
                        && curP.getY() >= fullExtent.getYMin() && curP.getY() <= fullExtent.getYMax()) {
                    zoomTo(locationDisplayManager.getPoint());
                }
                else {
                    Toast.makeText(BufferMapActivity.this, "当前位置不在地图范围内", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 缩放至特定的位置来显示
     */
    private void zoomTo(Geometry geometry){
        Envelope envelope = new Envelope();
        geometry.queryEnvelope(envelope);
        int vh = map.getMeasuredHeight();
        int vw = map.getMeasuredWidth();
        double eh = envelope.getHeight();
        double ew = envelope.getWidth();
        double res = Math.max(eh/vh,ew/vw);
        map.zoomToResolution(envelope.getCenter(),res);
    }

    /**
     * 打开定位服务设置界面
     **/
    private void openGPS(){
        locationManager = (LocationManager)BufferMapActivity.this.getSystemService(Context.LOCATION_SERVICE);
        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("系统提示");
            builder.setMessage("是否现在打开GPS?");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);

                }
            });
            builder.setCancelable(true);
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    private void drawNearBuffer(final double x, final double y, final double radius) {
        bufferLayer.removeAll();
        SimpleFillSymbol bufferSymbol = new SimpleFillSymbol(
                ResourcesCompat.getColor(getResources(), R.color.buffer, null), SimpleFillSymbol.STYLE.SOLID);
        bufferSymbol.setOutline(new SimpleLineSymbol(
                ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null), 1.0f, SimpleLineSymbol.STYLE.SOLID));
        Graphic buffer = new Graphic(drawCircle(x, y, radius, 100), bufferSymbol);
        bufferLayer.addGraphic(buffer);
    }

    private Polygon drawCircle(double x, double y, double radius, int pointsCount) {
        Polygon p = new Polygon();
        boolean started = false;
        double slice = 2 * Math.PI / pointsCount;
        for (int i = 0; i <= pointsCount; i++) {
            double rad = slice * i;
            double px = x + radius * Math.cos(rad);
            double py = y + radius * Math.sin(rad);
            Point point = new Point(px, py);
            if (started) {
                p.lineTo(point);
            } else {
                p.startPath(point);
                started = true;
            }
        }
        return p;
    }

    private void getNearUGO(final double x, final double y, final double radius) {
        searchGreenLand = new ArrayList<>();
        searchAncientTree = new ArrayList<>();
        searchStreetTree = new ArrayList<>();
        loadingDialog.setMessage("正在加载数据...");
        loadingDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String errorMsg[] = new String[1];
                List<GreenObject> UGO_list = WebServiceUtils.getNearUGO(x, y, radius, errorMsg);
                createUGOLayers(UGO_list);
                loadingDialog.dismiss();
                invalidateOptionsMenu();
            }
        }).start();
    }

    private void createUGOLayers(List<GreenObject> list) {
        greenLandLayer.removeAll();
        ancientTreeLayer.removeAll();
        streetTreeLayer.removeAll();
        searchGreenLand.clear();
        searchAncientTree.clear();
        searchStreetTree.clear();
        selectedUGOs.clear();
        if(list == null || list.size() == 0)
            return;
        for(GreenObject obj : list) {
            Geometry geometry = GeoJsonUtil.String2Geometry(obj.UGO_Geo_Location);
            if(geometry != null) {
                Map<String,Object> greenObj = new HashMap<>();
                if (obj.UGO_ClassType_ID.equals(GreenLandType)) {
                    searchGreenLand.add(obj);
                    greenObj.put("UGO_ID", obj.UGO_ID);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(GreenLandType), greenObj);
                    greenLandLayer.addGraphic(graphic);
                }
                else if (obj.UGO_ClassType_ID.equals(AncientTreeType)) {
                    searchAncientTree.add(obj);
                    greenObj.put("UGO_ID", obj.UGO_ID);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(AncientTreeType), greenObj);
                    ancientTreeLayer.addGraphic(graphic);
                }
                else if (obj.UGO_ClassType_ID.equals(StreetTreeType)){
                    searchStreetTree.add(obj);
                    greenObj.put("UGO_ID", obj.UGO_ID);
                    Graphic graphic = new Graphic(geometry, symbolMap.get(StreetTreeType), greenObj);
                    streetTreeLayer.addGraphic(graphic);
                }
            }
        }
        selectedUGOs.addAll(searchAncientTree);
        selectedUGOs.addAll(searchGreenLand);
        selectedUGOs.addAll(searchStreetTree);
        greenLandLayer.setSelectedGraphics(greenLandLayer.getGraphicIDs(), true);
        ancientTreeLayer.setSelectedGraphics(ancientTreeLayer.getGraphicIDs(), true);
        streetTreeLayer.setSelectedGraphics(streetTreeLayer.getGraphicIDs(), true);
    }

    private void createBufferCenterLayer() {
        PictureMarkerSymbol pinSymbol = new PictureMarkerSymbol(new BitmapDrawable(
                BitmapFactory.decodeResource(getResources(), R.drawable.center_pin)));
        // a half of the picture's height
        pinSymbol.setOffsetY(16);
        Geometry centerGeo = new Point(fullExtent.getCenterX(), fullExtent.getCenterY());
        Graphic centerGraphic = new Graphic(centerGeo, pinSymbol);
        bufferCenterPinLayer.addGraphic(centerGraphic);
        pinGraphicID = bufferCenterPinLayer.getGraphicIDs()[0];
    }
}
