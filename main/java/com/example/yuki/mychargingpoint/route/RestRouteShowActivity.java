package com.example.yuki.mychargingpoint.route;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.model.AMapCarInfo;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapRestrictionInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.example.yuki.mychargingpoint.R;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.example.yuki.mychargingpoint.basic.BasicMapActivity;
import com.example.yuki.mychargingpoint.basic.TwoMapActivity;
import com.example.yuki.mychargingpoint.util.AddMarker;
import com.example.yuki.mychargingpoint.util.Constants;
import com.example.yuki.mychargingpoint.util.DialogFragmentSetting;
import com.example.yuki.mychargingpoint.util.SharedHelper;
import com.example.yuki.mychargingpoint.weather.WeatherSearchActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RestRouteShowActivity extends AppCompatActivity implements AMapNaviListener, OnClickListener, OnCheckedChangeListener, AMap.InfoWindowAdapter {
    private boolean congestion, cost, hightspeed, avoidhightspeed;
    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;
    private AMap aMap;
    private AddMarker addMarker = new AddMarker();
    private Context context;
    /**
     * 地图对象
     */
    private MapView mRouteMapView;
    private Marker mStartMarker;
    private Marker mEndMarker;
    /**
     * 选择起点Action标志位
     */
    private boolean mapClickStartReady;
    private boolean mapClickwayReady;
    /**
     * 选择终点Aciton标志位
     */
    private boolean mapClickEndReady;
    private boolean mymarkerbool02 = false;
    private NaviLatLng startLatlng = new NaviLatLng(39.925041, 116.437901);
    private NaviLatLng endLatlng = new NaviLatLng(39.955846, 116.352765);
    private NaviLatLng wayLatlng = new NaviLatLng(39.955846, 116.352765);
    private List<NaviLatLng> startList = new ArrayList<NaviLatLng>();
    /**
     * 途径点坐标集合
     */
    private List<NaviLatLng> wayList = new ArrayList<NaviLatLng>();
    /**
     * 终点坐标集合［建议就一个终点］
     */
    private List<NaviLatLng> endList = new ArrayList<NaviLatLng>();
    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    private Marker mWayMarker;
    /**
     * 当前用户选中的路线，在下个页面进行导航
     */
    private int routeIndex;
    /**
     * 路线的权值，重合路线情况下，权值高的路线会覆盖权值低的路线
     **/
    private int zindex = 1;
    /**
     * 路线计算成功标志位
     */
    private boolean calculateSuccess = false;
    private boolean chooseRouteSuccess = false;
    final private int BasicMap = 110;
    final private int TwoMap = 111;
    final private int RestRoute = 112;
    final private int RoutePoint = 113;
    final private int WeatherSearch = 114;
    final private int Setting = 115;
    private SharedHelper sh;
    private Map map;

    //    RouteChose routeChose = new RouteChose();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rest_calculate);
        CheckBox congestion = (CheckBox) findViewById(R.id.congestion);
        CheckBox cost = (CheckBox) findViewById(R.id.cost);
        CheckBox hightspeed = (CheckBox) findViewById(R.id.hightspeed);
        CheckBox avoidhightspeed = (CheckBox) findViewById(R.id.avoidhightspeed);
        Button calculate = (Button) findViewById(R.id.calculate);
        Button startPoint = (Button) findViewById(R.id.startpoint);
        Button wayPoint = (Button) findViewById(R.id.waypoint);
        Button endPoint = (Button) findViewById(R.id.endpoint);
        Button selectroute = (Button) findViewById(R.id.selectroute);
        Button gpsnavi = (Button) findViewById(R.id.gpsnavi);
        Button emulatornavi = (Button) findViewById(R.id.emulatornavi);
        Button mypoint2 = (Button) findViewById(R.id.mypoint2);
        calculate.setOnClickListener(this);
        startPoint.setOnClickListener(this);
        wayPoint.setOnClickListener(this);
        endPoint.setOnClickListener(this);
        selectroute.setOnClickListener(this);
        gpsnavi.setOnClickListener(this);
        emulatornavi.setOnClickListener(this);
        mypoint2.setOnClickListener(this);
        congestion.setOnCheckedChangeListener(this);
        cost.setOnCheckedChangeListener(this);
        hightspeed.setOnCheckedChangeListener(this);
        avoidhightspeed.setOnCheckedChangeListener(this);

        mRouteMapView = (MapView) findViewById(R.id.navi_view);
        mRouteMapView.onCreate(savedInstanceState);
        context = getApplicationContext();
        aMap = mRouteMapView.getMap();
//        aMap.setOnMarkerClickListener(this);
//        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                //控制选起点
                if (mapClickStartReady) {
                    startLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mStartMarker.setPosition(latLng);
                    startList.clear();
                    wayList.clear();
                    startList.add(startLatlng);
                    mapClickStartReady = false;
                }
                //控制选途经点
                if (mapClickwayReady) {
                    wayLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mWayMarker.setPosition(latLng);
//                    wayList.clear();
                    wayList.add(wayLatlng);
                    mapClickwayReady = false;
                }
                //控制选终点
                if (mapClickEndReady) {
                    endLatlng = new NaviLatLng(latLng.latitude, latLng.longitude);
                    mEndMarker.setPosition(latLng);
                    endList.clear();
                    endList.add(endLatlng);
                    mapClickEndReady = false;
                }

            }
        });
        // 初始化Marker添加到地图
        mStartMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.start))));
        mWayMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.through))));
        mEndMarker = aMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.end))));

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                setUp(aMap);
            }
        });
//        routeChose.RouteChoseContextAMap(context,aMap);
        initLocation();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menu.add(1, BasicMap, 1, R.string.action_BasicMap);
        menu.add(1, TwoMap, 2, R.string.action_TwoMap);
        menu.add(1, RestRoute, 3, R.string.action_RestRoute);
        menu.add(1, RoutePoint, 4, R.string.action_RoutePoint);
        menu.add(1, WeatherSearch, 5, R.string.action_WeatherSearch);
        menu.add(1, Setting, 6, R.string.action_settings);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case BasicMap:
                BasicMapActivity.instance.finish();
                startActivity(new Intent(RestRouteShowActivity.this, BasicMapActivity.class));
                finish();
                break;
            case TwoMap:
                startActivity(new Intent(RestRouteShowActivity.this, TwoMapActivity.class));
                finish();
                break;
            case RestRoute:
                startActivity(new Intent(RestRouteShowActivity.this, RestRouteShowActivity.class));
                finish();
                break;
            case RoutePoint:
                startActivity(new Intent(RestRouteShowActivity.this, RoutePointActivity.class));
                finish();
                break;
            case WeatherSearch:
                startActivity(new Intent(RestRouteShowActivity.this, WeatherSearchActivity.class));
                break;
            case Setting:
                DialogFragmentSetting dialog = new DialogFragmentSetting();
                dialog.DialogFragmentSetting(context);
                dialog.show(getFragmentManager(), "loginDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 界面属性设置
     */
    private void setUp(AMap amap) {

        UiSettings uiSettings = amap.getUiSettings();
        amap.showIndoorMap(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

    /**
     * 定位蓝点
     */
    private void initLocation() {
        MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        //自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(5);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mRouteMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mRouteMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mRouteMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        startList.clear();
        wayList.clear();
        endList.clear();
        routeOverlays.clear();
        mRouteMapView.onDestroy();
        /**
         * 当前页面只是展示地图，activity销毁后不需要再回调导航的状态
         */
        mAMapNavi.removeAMapNaviListener(this);
        mAMapNavi.destroy();

    }

    /**
     * 多选设定
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.congestion:
                congestion = isChecked;
                break;
            case R.id.avoidhightspeed:
                avoidhightspeed = isChecked;
                break;
            case R.id.cost:
                cost = isChecked;
                break;
            case R.id.hightspeed:
                hightspeed = isChecked;
                break;
            default:
                break;
        }
    }

    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        //清空上次计算的路径列表。
        routeOverlays.clear();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
            }
        }
    }

    @Override
    public void onCalculateRouteSuccess() {
        /**清空上次计算的路径列表**/
        routeOverlays.clear();
        AMapNaviPath path = mAMapNavi.getNaviPath();
        /**单路径不需要进行路径选择，直接传入－1即可**/
        drawRoutes(-1, path);
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        calculateSuccess = false;
        Toast.makeText(getApplicationContext(), "计算路线失败，errorcode＝" + arg0, Toast.LENGTH_SHORT).show();
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        calculateSuccess = true;
        aMap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(aMap, path, this);
        routeOverLay.setTrafficLine(false);
        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
    }

    public void changeRoute() {
        if (!calculateSuccess) {
            Toast.makeText(this, "请先算路", Toast.LENGTH_SHORT).show();
            return;
        }
        /**
         * 计算出来的路径只有一条
         */
        if (routeOverlays.size() == 1) {
            chooseRouteSuccess = true;
            //必须告诉AMapNavi 你最后选择的哪条路
            mAMapNavi.selectRouteId(routeOverlays.keyAt(0));
            Toast.makeText(this, "导航距离:" + (mAMapNavi.getNaviPath()).getAllLength() + "m" + "\n" + "导航时间:" + (mAMapNavi.getNaviPath()).getAllTime() + "s", Toast.LENGTH_SHORT).show();
            return;
        }

        if (routeIndex >= routeOverlays.size())
            routeIndex = 0;
        int routeID = routeOverlays.keyAt(routeIndex);
        //突出选择的那条路
        for (int i = 0; i < routeOverlays.size(); i++) {
            int key = routeOverlays.keyAt(i);
            routeOverlays.get(key).setTransparency(0.4f);
        }
        routeOverlays.get(routeID).setTransparency(1);
        /**把用户选择的那条路的权值弄高，使路线高亮显示的同时，重合路段不会变的透明**/
        routeOverlays.get(routeID).setZindex(zindex++);

        //必须告诉AMapNavi 你最后选择的哪条路
        mAMapNavi.selectRouteId(routeID);
        Toast.makeText(this, "路线标签:" + mAMapNavi.getNaviPath().getLabels(), Toast.LENGTH_SHORT).show();
        routeIndex++;
        chooseRouteSuccess = true;

        /**选完路径后判断路线是否是限行路线**/
        AMapRestrictionInfo info = mAMapNavi.getNaviPath().getRestrictionInfo();
        if (!TextUtils.isEmpty(info.getRestrictionTitle())) {
            Toast.makeText(this, info.getRestrictionTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 清除当前地图上算好的路线
     */
    private void clearRoute() {
        for (int i = 0; i < routeOverlays.size(); i++) {
            RouteOverLay routeOverlay = routeOverlays.valueAt(i);
            routeOverlay.removeFromMap();
        }
        routeOverlays.clear();
    }

    /**
     * 算路径
     */
    private void calculate() {
        clearRoute();
        mapClickStartReady = false;
        mapClickEndReady = false;
        if (avoidhightspeed && hightspeed) {
            Toast.makeText(getApplicationContext(), "不走高速与高速优先不能同时为true.", Toast.LENGTH_LONG).show();
        }
        if (cost && hightspeed) {
            Toast.makeText(getApplicationContext(), "高速优先与避免收费不能同时为true.", Toast.LENGTH_LONG).show();
        }
            /*
             * strategyFlag转换出来的值都对应PathPlanningStrategy常量，用户也可以直接传入PathPlanningStrategy常量进行算路。
			 * 如:mAMapNavi.calculateDriveRoute(mStartList, mEndList, mWayPointList,PathPlanningStrategy.DRIVING_DEFAULT);
			 */
        int strategyFlag = 0;
        try {
            strategyFlag = mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (strategyFlag >= 0) {
            String carNumber = null;
            AMapCarInfo carInfo = new AMapCarInfo();
            try {
                sh = new SharedHelper(context);
                map = sh.read();
                carNumber = map.get("carnumber").toString();
            } catch (Exception e) {

            }
//            String regex = ".*[a-zA-Z]+.*";
//            Matcher m = Pattern.compile(regex).matcher(carNumber);
            if (carNumber!=null) {
                //设置车牌
                carInfo.setCarNumber(carNumber);
                //设置车牌是否参与限行算路
                carInfo.setRestriction(true);
            } else {
                carInfo.setRestriction(false);
            }
            mAMapNavi.setCarInfo(carInfo);
            mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag);
//            Toast.makeText(getApplicationContext(), "策略:" + strategyFlag, Toast.LENGTH_LONG).show();
//                    wayList.add(new NaviLatLng(Constants.chargingpointbaicheng03.latitude, Constants.chargingpointbaicheng03.longitude));
//                    for (int i = 1; i < 3; i++) {
//                        length3 = i;
//                        Log.w("主页0000", "" + i);
//                        int re1 = 0;
//                        try {
//                            Intent intent1 = new Intent();
//                            intent1.setClassName(this, "RouteChose.class");
//                            startActivity(intent1);
//                            Log.w("主页0001", "OK");
//                        } catch (Exception e) {
//                            Log.w("主页0001", "WRONG01");
//                        }
//                        try {
//                            wayList2.clear();
//                            wayList2.add(addMarker.getNaviLatLng(i));
//                            routeChose.RouteChose0(startList, endList, wayList2);
//                            routeChose.DriveRoute();
//                            Timer timer = new Timer();//实例化Timer类
//                            timer.schedule(new TimerTask() {
//                                public void run() {
//                                    this.cancel();
//                                }
//                            }, 1000);
//                            Thread thread=new Thread();
//                            thread.sleep(1000);
//                            re1 = routeChose.DriveRoute();
//                            routeOverlays.clear();
//                            Log.w("主页0002", "OK");
//                        } catch (Exception e) {
//                            Log.w("主页0002", "WRONG02");
//                        }

//                        try {
//                            int re;
//                            RouteChose routeChose = new RouteChose();
//                            re = routeChose.RouteChose(startList, endList, wayList);
//                            if (length1 == 0) {
//                                length1 = re;
//                            } else {
//                                if (length1 >= re) {
//                                    length1 = re;
//                                    length2 = i;
//                                }
//                            }
//                        } catch (Exception e) {
//                            Toast.makeText(getApplicationContext(), "wrong3", Toast.LENGTH_LONG).show();
//                        }
//                    }
//                    wayList.clear();
//                    wayList.add(new NaviLatLng(marker3[length2].getPosition().latitude, marker3[length2].getPosition().longitude));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.calculate:
                calculate();
                break;
            case R.id.startpoint:
                Toast.makeText(this, "请在地图上点选起点", Toast.LENGTH_SHORT).show();
                mapClickStartReady = true;
                break;
            case R.id.waypoint:
                Toast.makeText(this, "请在地图上点选途经点", Toast.LENGTH_SHORT).show();
                mapClickwayReady = true;
                break;
            case R.id.endpoint:
                Toast.makeText(this, "请在地图上点选终点", Toast.LENGTH_SHORT).show();
                mapClickEndReady = true;
                break;
            case R.id.selectroute:
                changeRoute();
                break;
            case R.id.gpsnavi:
                Intent gpsintent = new Intent(getApplicationContext(), RouteNaviActivity.class);
                gpsintent.putExtra("gps", true);
                gpsintent.putExtra("getNavi", false);
                startActivity(gpsintent);
                break;
            case R.id.emulatornavi:
                Intent intent = new Intent(getApplicationContext(), RouteNaviActivity.class);
                intent.putExtra("gps", false);
                intent.putExtra("getNavi", false);
                startActivity(intent);
                break;
            case R.id.mypoint2:
                if (mymarkerbool02) {
                    mymarkerbool02 = false;
                    addMarker.MarkerRemove();
                } else {
                    mymarkerbool02 = true;
                    addMarker.addMarkersToMap(aMap);// 往地图上添加marker
                }
                break;
            default:
                break;
        }
    }

    //自定义marker点击弹窗内容
    @Override
    public View getInfoWindow(final Marker marker) {
        View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri02,
                null);
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(marker.getTitle());
        TextView snippet = (TextView) view.findViewById(R.id.snippet);
        snippet.setText("" + marker.getSnippet());
        ImageButton button = (ImageButton) view
                .findViewById(R.id.start_amap_app);
        //
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wayLatlng = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mWayMarker.setPosition(marker.getPosition());
                wayList.add(wayLatlng);
                Toast.makeText(context, "已选择"+marker.getTitle()+"为途经点", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton button02 = (ImageButton) view
                .findViewById(R.id.start_amap_app02);
        //
        button02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endLatlng = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                mEndMarker.setPosition(marker.getPosition());
                endList.clear();
                endList.add(endLatlng);
                Toast.makeText(context, "已选择"+marker.getTitle()+"为途经点", Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    /**
     * ************************************************** 在算路页面，以下接口全不需要处理，在以后的版本中我们会进行优化***********************************************************************************************
     **/

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo arg0) {


    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo arg0) {


    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] arg0) {


    }

    @Override
    public void hideCross() {


    }

    @Override
    public void hideLaneInfo() {


    }

    @Override
    public void notifyParallelRoad(int arg0) {


    }

    @Override
    public void onArriveDestination() {


    }

    @Override
    public void onArrivedWayPoint(int arg0) {


    }

    @Override
    public void onEndEmulatorNavi() {


    }

    @Override
    public void onGetNavigationText(int arg0, String arg1) {


    }

    @Override
    public void onGpsOpenStatus(boolean arg0) {


    }

    @Override
    public void onInitNaviFailure() {


    }

    @Override
    public void onLocationChange(AMapNaviLocation arg0) {


    }

    @Override
    public void onNaviInfoUpdate(NaviInfo arg0) {


    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo arg0) {


    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] amapServiceAreaInfos) {

    }

    @Override
    public void onReCalculateRouteForTrafficJam() {


    }

    @Override
    public void onReCalculateRouteForYaw() {


    }

    @Override
    public void onStartNavi(int arg0) {


    }

    @Override
    public void onTrafficStatusUpdate() {


    }

    @Override
    public void showCross(AMapNaviCross arg0) {


    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] arg0, byte[] arg1, byte[] arg2) {


    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo arg0) {


    }

    @Override
    public void onPlayRing(int i) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat arg0) {


    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
