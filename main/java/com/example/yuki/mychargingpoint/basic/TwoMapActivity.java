package com.example.yuki.mychargingpoint.basic;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.yuki.mychargingpoint.R;
import com.example.yuki.mychargingpoint.route.RestRouteShowActivity;
import com.example.yuki.mychargingpoint.route.RoutePointActivity;
import com.example.yuki.mychargingpoint.util.DialogFragmentSetting;
import com.example.yuki.mychargingpoint.weather.WeatherSearchActivity;

/**
 * 多实例地图实现
 */
public class TwoMapActivity extends AppCompatActivity {

    MapView mapView;
    MapView textureMapView;

    AMap aMap1;
    AMap aMap2;
    private Context context;
    final private int BasicMap = 110;
    final private int TwoMap = 111;
    final private int RestRoute = 112;
    final private int RoutePoint = 113;
    final private int WeatherSearch =114;
    final private int Setting = 115;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_two_map);

        mapView = (MapView) findViewById(R.id.mapview);
        textureMapView = (MapView) findViewById(R.id.texturemapview);


        mapView.onCreate(savedInstanceState);
        textureMapView.onCreate(savedInstanceState);

        aMap1 = mapView.getMap();
        aMap2 = textureMapView.getMap();

        init();
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
//        menu.add(1,GRAY,5,"灰色");
//        menu.add(1,CYAN,6,"蓝绿色");
//        menu.add(1,BLACK,7,"黑色");
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
                startActivity(new Intent(TwoMapActivity.this, BasicMapActivity.class));
                finish();
                break;
            case TwoMap:
                startActivity(new Intent(TwoMapActivity.this, TwoMapActivity.class));
                finish();
                break;
            case RestRoute:
                startActivity(new Intent(TwoMapActivity.this, RestRouteShowActivity.class));
                finish();
                break;
            case RoutePoint:
                startActivity(new Intent(TwoMapActivity.this, RoutePointActivity.class));
                finish();
                break;
            case WeatherSearch:
                startActivity(new Intent(TwoMapActivity.this, WeatherSearchActivity.class));
                break;
            case Setting:
                context=getApplicationContext();
                DialogFragmentSetting dialog = new DialogFragmentSetting();
                dialog.DialogFragmentSetting(context);
                dialog.show(getFragmentManager(), "loginDialog");
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 地图初始化完成后
     */
    private void init() {
        aMap1.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                setUp(aMap1);
            }
        });

        aMap2.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                setUp(aMap2);
            }
        });
    }

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
        aMap1.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap1.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        aMap2.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);设置默认定位按钮是否显示，非必需设置。
        aMap2.setMyLocationEnabled(true);
    }

    private void setUp(AMap amap) {
        UiSettings uiSettings = amap.getUiSettings();
        amap.showIndoorMap(true);
        uiSettings.setCompassEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        textureMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        textureMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
        textureMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        textureMapView.onDestroy();
    }
}
