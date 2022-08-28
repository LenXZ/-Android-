package com.example.yuki.mychargingpoint.basic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.example.yuki.mychargingpoint.R;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.example.yuki.mychargingpoint.location.Utils;
import com.example.yuki.mychargingpoint.route.RestRouteShowActivity;
import com.example.yuki.mychargingpoint.route.RouteNaviActivity;
import com.example.yuki.mychargingpoint.route.RoutePointActivity;
import com.example.yuki.mychargingpoint.util.AddMarker;
import com.example.yuki.mychargingpoint.util.DialogFragmentSetting;
import com.example.yuki.mychargingpoint.util.ToastUtil;
import com.example.yuki.mychargingpoint.view.SettingActivity;
import com.example.yuki.mychargingpoint.weather.WeatherSearchActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * 基本地图
 */
public class BasicMapActivity extends AppCompatActivity implements AMapLocationListener, AMap.OnMapClickListener, AMap.OnMapLongClickListener,
        AMap.InfoWindowAdapter, AMap.OnMarkerClickListener,
        OnClickListener, OnPoiSearchListener {
    private AMap aMap;//初始化地图控制器对象
    private AMapLocation mCurrentLocation;
    private AMapLocationClient mlocationClient = null;
    private AMapLocationClientOption mLocationOption = null;
    private AddMarker addMarker = new AddMarker();
    private BasicMapActivity.myPoiOverlay poiOverlay;// poi图层
    private Button basicmap;
    private Button rsmap;
    private Button nightmap;
    private Button navimap;
    private Button rotatemap;
    private Button mypoint0;
    private boolean gpsbuttonbool = false;//判断路径规划按钮是否按下
    private boolean mymarkerbool = false;//判断是充电点和自选点 还是搜索poi
    private boolean mymarkerbool01 = false;//判断是地图上有无搜索标记
    private boolean mymarkerbool02 = false;//判断地图上有无充电点标记
    private boolean mymarkerbool03 = false;//判断地图上有无长按标记
    private boolean mymarkerbool04 = false;//判断是否点击了充电点标记
    private Context context;
    private Double longitude, latitude;//经度纬度
    private EditText mSearchText;
    private LatLonPoint lp;// 116.472995,39.993743
    private List<PoiItem> poiItems;// poi数据
    private int currentPage = 0;// 当前页面，从0开始计数
    private Marker mlastMarker;
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker marker2;// 有跳动效果的marker对象
    private MarkerOptions markerOptions;
    private MapView mapView = null;
    private MyLocationStyle myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类
    private NaviLatLng endLat = null;
    private RelativeLayout mPoiDetail;
    private PoiResult poiResult; // poi返回的结果
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private String keyWord = "";
    private TextView mPoiName, mPoiAddress;
    //1.定义不同颜色的菜单项的标识:
    final private int BasicMap = 110;
    final private int TwoMap = 111;
    final private int RestRoute = 112;
    final private int RoutePoint = 113;
    final private int WeatherSearch = 114;
    final private int Setting = 115;
    public static BasicMapActivity instance = null;

    //    private PoiOverlay poiOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basicmap_activity);
        instance = this;
        /*
         * 设置离线地图存储目录，在下载离线地图或初始化地图设置;
         * 使用过程中可自行设置, 若自行设置了离线地图存储的路径，
         * 则需要在离线地图下载和使用地图页面都进行路径设置
         * */
        //Demo中为了其他界面可以使用下载的离线地图，使用默认位置存储，屏蔽了自定义设置
        //  MapsInitializer.sdcardDir =OffLineMapUtils.getSdCacheDir(this);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        initLocationbest();
        init();
        initLocation();
    }

    /**
     * 菜单选项
     */
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

    /**
     * 菜单操作
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case BasicMap:
                startActivity(new Intent(BasicMapActivity.this, BasicMapActivity.class));
                finish();
                break;
            case TwoMap:
                startActivity(new Intent(BasicMapActivity.this, TwoMapActivity.class));
                break;
            case RestRoute:
                startActivity(new Intent(BasicMapActivity.this, RestRouteShowActivity.class));
                break;
            case RoutePoint:
                startActivity(new Intent(BasicMapActivity.this, RoutePointActivity.class));
                break;
            case WeatherSearch:
                startActivity(new Intent(BasicMapActivity.this, WeatherSearchActivity.class));
                break;
            case Setting:
                context = getApplicationContext();
                DialogFragmentSetting dialog = new DialogFragmentSetting();
                dialog.DialogFragmentSetting(context);
                dialog.show(getFragmentManager(), "loginDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        /**点击返回键*/
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mymarkerbool01) {
                refresh();
                mymarkerbool01 = false;
            } else if (mymarkerbool02) {
                if (mymarkerbool04) {
                    refresh();
                }
                addMarker.MarkerRemove();
                aMap.removecache();
//                addMarker.MarkerRemove(aMap);
                mymarkerbool02 = false;
            } else if (mymarkerbool03) {
                try {
                    marker2.destroy();
                    aMap.removecache();
                    mymarkerbool03 = false;
                } catch (Exception e) {

                }
            } else {
                // 创建退出对话框
                AlertDialog isExit = new AlertDialog.Builder(this).create();
                // 设置对话框标题
                isExit.setTitle("系统提示");
                // 设置对话框消息
                isExit.setMessage("确定要退出吗");
                // 添加选择按钮并注册监听
                isExit.setButton("确定", listener);
                isExit.setButton2("取消", listener);
                // 显示对话框
                isExit.show();
            }

        }
        return false;
    }

    /**
     * 监听对话框里面的button点击事件
     */
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    finish();
                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 获取定位坐标
     */
    public void initLocationbest() {
        mlocationClient = new AMapLocationClient(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。
        //如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会。
        mLocationOption.setOnceLocationLatest(true);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {

        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                latitude = amapLocation.getLatitude();
                longitude = amapLocation.getLongitude();
//				ToastUtil.show(BasicMapActivity.this, getString(R.string.welcometxt));
                lp = new LatLonPoint(latitude, longitude);
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        } else {
        }
        mCurrentLocation = amapLocation;
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMapClickListener(this);
            aMap.setOnMapLongClickListener(this);
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
            TextView searchButton = (TextView) findViewById(R.id.btn_search);
            searchButton.setOnClickListener(this);
        }
        setMapCustomStyleFile(this);
        basicmap = (Button) findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = (Button) findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = (Button) findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        rotatemap = (Button) findViewById(R.id.gps_rotate_button);
        rotatemap.setOnClickListener(this);
        navimap = (Button) findViewById(R.id.navimap);
        navimap.setOnClickListener(this);
        mypoint0 = (Button) findViewById(R.id.mypoint0);
        mypoint0.setOnClickListener(this);
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail01);
        mPoiDetail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				Intent intent = new Intent(PoiSearchActivity.this,
//						SearchDetailActivity.class);
//				intent.putExtra("poiitem", mPoi);
//				startActivity(intent);
            }
        });
        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                setUp(aMap);
            }
        });
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        keyWord = mSearchText.getText().toString().trim();
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", "");// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
        locationMarker.showInfoWindow();
        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    /**
     * 定位蓝点
     */
    private void initLocation() {
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.
                fromResource(R.drawable.gps_point));
        myLocationStyle.strokeColor(Color.argb(180, 3, 145, 255));
        myLocationStyle.strokeWidth(5);
        myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 180));
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    private void setUp(AMap amap) {
        UiSettings uiSettings = amap.getUiSettings();
        amap.showIndoorMap(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
        mSearchText = (EditText) findViewById(R.id.input_edittext);
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay != null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new BasicMapActivity.myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();
                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));
                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(lp.getLatitude(),
                                        lp.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));
                        mymarkerbool01 = true;
                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(BasicMapActivity.this,
                                R.string.no_result);
                    }
                }
            } else {
                ToastUtil
                        .show(BasicMapActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this.getApplicationContext(), rcode);
        }
    }

    private int[] markers = {
            R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);
        } else {
            mPoiDetail.setVisibility(View.GONE);
        }
    }

    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory
                    .fromBitmap(BitmapFactory.decodeResource(
                            getResources(),
                            markers[index])));
        } else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }

    @Override
    public void onMapClick(LatLng latLng) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(marker2)) {
            marker2.showInfoWindow();
            mymarkerbool = true;
            return false;
        } else if (marker.getTitle().startsWith("充电点")) {
            int markerId = Integer.parseInt(marker.getTitle().toString().subSequence(3, 5).toString());
//            ToastUtil.show(this, ""+markerId);
            addMarker.marker3[markerId].showInfoWindow();
            mymarkerbool = true;
            return false;
        } else if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);

            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
//                detailMarker = aMap.addMarker(new MarkerOptions()
//                        .icon(BitmapDescriptorFactory
//                                .fromBitmap(BitmapFactory.decodeResource(
//                                        getResources(),
//                                        R.drawable.poi_marker_pressed))));
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.poi_marker_pressed)));

                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        } else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }
        return false;
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }

    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet() + mCurrentPoi.getDistance());
    }

    /**
     * 长按地图
     */

    @Override
    public void onMapLongClick(LatLng latLng) {
//        try {
//            marker2.remove();
//        } catch (Exception e) {
//        }
        try {
            marker2.destroy();
            aMap.removecache();
        } catch (Exception e) {
        }
        markerOptions = new MarkerOptions().title("点击红色按钮进入路径规划").snippet("点击蓝色按钮开始模拟导航").position(
                latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.pointmarkerstar));
        mymarkerbool03 = true;
        marker2 = aMap.addMarker(markerOptions);
        marker2.showInfoWindow();
        mymarkerbool = true;
//                icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
    }

    /**
     * 自定义PoiOverlay
     */

    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();

        public myPoiOverlay(AMap amap, List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         *
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
//                mark.destroy();
            }
        }

        /**
         * 移动镜头到当前的视角。
         *
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        public float getDistance(int index) {
            return mPois.get(index).getDistance();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         *
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            } else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }

    private void setMapCustomStyleFile(Context context) {
        String styleName = "style_json.json";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = context.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = context.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(b);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();

                if (outputStream != null)
                    outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        aMap.setCustomMapStylePath(filePath + "/" + styleName);

//		aMap.showMapText(false);//取消地名显示

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        whetherToShowDetailInfo(false);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        mlocationClient.unRegisterLocationListener(this);
        mlocationClient = null;
    }

    public void refresh() {
        finish();
        Intent intent = new Intent(BasicMapActivity.this, BasicMapActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basicmap:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                if (gpsbuttonbool) {
                    refresh();
                }
                break;
            case R.id.rsmap:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;
            case R.id.nightmap:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
                break;
            case R.id.gps_rotate_button:
                gpsbuttonbool = true;
//                myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
                aMap.setMyLocationStyle(myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE_NO_CENTER));//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。
                break;
            case R.id.navimap:
                Intent intent = new Intent(this, RoutePointActivity.class);
                if (endLat == null) {
                    startActivity(intent);
                } else {
                    intent.putExtra("getNavi", true);
                    intent.putExtra("end", endLat);
                    startActivity(intent);
                }
//                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
            case R.id.btn_search:
                doSearchQuery();
                break;
            case R.id.mypoint0:
                if (mymarkerbool02) {
                    mymarkerbool02 = false;
                    addMarker.MarkerRemove();
                } else {
                    mymarkerbool02 = true;
                    addMarker.addMarkersToMap(aMap);// 往地图上添加marker
                }
//                mymarkerbool01 = true;
                break;
            default:
                break;
        }

//		mStyleCheckbox.setChecked(false);

    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    //点击一键导航按钮跳转到导航页面
    private void startAMapNavi(Marker marker) {
        if (mCurrentLocation == null) {
            return;
        }
//        Intent intent = new Intent(getApplicationContext(), RouteNaviActivity.class);
//        intent.putExtra("gps", false);
//        startActivity(intent);
//        Intent intent = new Intent(this, RouteNaviActivity01.class);
        Intent intent = new Intent(this, RouteNaviActivity.class);
        intent.putExtra("gps", false);
        intent.putExtra("getNavi", true);
        intent.putExtra("start", new NaviLatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude()));
        intent.putExtra("end", new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude));
        startActivity(intent);
    }

    private void startPointNavi() {
        Intent intent = new Intent(this, RoutePointActivity.class);
        if (endLat == null) {
            startActivity(intent);
        } else {
            intent.putExtra("getNavi", true);
            intent.putExtra("end", endLat);
            startActivity(intent);
        }
    }

    //自定义marker点击弹窗内容
    @Override
    public View getInfoWindow(final Marker marker) {
        endLat = new NaviLatLng(marker.getPosition().latitude, marker.getPosition().longitude);
        if (mymarkerbool == true) {/**充电点OR自选点*/
            mymarkerbool = false;
            mymarkerbool04 = true;
            View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri,
                    null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(marker.getTitle());
            TextView snippet = (TextView) view.findViewById(R.id.snippet);
            snippet.setText("" + marker.getSnippet());
            ImageButton button = (ImageButton) view
                    .findViewById(R.id.start_amap_app);
            // 路径规划
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startPointNavi();
                }
            });
            ImageButton button02 = (ImageButton) view
                    .findViewById(R.id.start_amap_app02);
            // 调起导航
            button02.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAMapNavi(marker);
                }
            });
            return view;
        } else {/**搜索poi*/
            View view = getLayoutInflater().inflate(R.layout.poikeywordsearch_uri01,
                    null);
            TextView title = (TextView) view.findViewById(R.id.title);
            title.setText(marker.getTitle());
            TextView snippet = (TextView) view.findViewById(R.id.snippet);
            int index = poiOverlay.getPoiIndex(marker);
            float distance = poiOverlay.getDistance(index);
            String showDistance = Utils.getFriendlyDistance((int) distance);
            snippet.setText("距当前位置" + showDistance);
            ImageButton button = (ImageButton) view
                    .findViewById(R.id.start_amap_app02);
            // 调起导航
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startAMapNavi(marker);
                }
            });
            return view;
        }
    }

}
