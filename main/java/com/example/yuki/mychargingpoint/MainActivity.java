package com.example.yuki.mychargingpoint;

import android.os.Bundle;

import com.example.yuki.mychargingpoint.route.RestRouteShowActivity;
import com.example.yuki.mychargingpoint.routepoi.RouteNaviActivity01;
import com.example.yuki.mychargingpoint.route.RoutePointActivity;
import com.example.yuki.mychargingpoint.view.FeatureView;

import android.view.View;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.amap.api.maps.MapsInitializer;
import com.example.yuki.mychargingpoint.basic.BasicMapActivity;
import com.example.yuki.mychargingpoint.basic.TwoMapActivity;

public final class MainActivity extends ListActivity {
    private static class DemoDetails {
        private final int titleId;
        private final int descriptionId;//描述ID
        private final Class<? extends android.app.Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId,
                           Class<? extends android.app.Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }
    }

    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }
            DemoDetails demo = getItem(position);
            featureView.setTitleId(demo.titleId, demo.activityClass != null);
            return featureView;
        }
    }

    private static final DemoDetails[] demos = {
//		            创建地图
//            new DemoDetails(R.string.map_create, R.string.blank, null),
//			显示地图
            new DemoDetails(R.string.basic_map, R.string.basic_description,
                    BasicMapActivity.class),
//			地图多实例
            new DemoDetails(R.string.multi_inst, R.string.blank,
                    TwoMapActivity.class),
            //			驾车出行路线规划
            new DemoDetails(R.string.RestRoute, R.string.RestRouteshow, RestRouteShowActivity.class),
            new DemoDetails(R.string.RestRoutePoint, R.string.RestRoutePoint, RoutePointActivity.class)
//		           室内地图
//            new DemoDetails(R.string.indoormap_demo, R.string.indoormap_description,
//                    IndoorMapActivity.class),
//-----------与地图交互-----------------------------------------------------------------------------------------------
//            new DemoDetails(R.string.map_interactive, R.string.blank, null),
//            //缩放控件、定位按钮、指南针、比例尺等的添加
//            new DemoDetails(R.string.uisettings_demo,
//                    R.string.uisettings_description, UiSettingsActivity.class),
//            //监听点击、长按、拖拽地图等事件events
//            new DemoDetails(R.string.events_demo, R.string.events_description,
//                    EventsActivity.class),
//            //地图POI点击
//            new DemoDetails(R.string.poiclick_demo,
//                    R.string.poiclick_description, PoiClickActivity.class),
//            //3D地图效果
//            new DemoDetails(R.string.threeD_map, R.string.animate_description,
//                    Animate_CameraActivity.class),
//            //获取地图数据
//            new DemoDetails(R.string.search_data, R.string.blank, null),
//            //关键字检索
//            new DemoDetails(R.string.poikeywordsearch_demo,
//                    R.string.poikeywordsearch_description,
//                    PoiKeywordSearchActivity.class),
//            //周边搜索
//            new DemoDetails(R.string.poiaroundsearch_demo,
//                    R.string.poiaroundsearch_description,
//                    PoiAroundSearchActivity.class),
//            //沿途搜索
//            new DemoDetails(R.string.routepoisearch_demo,
//                    R.string.routepoisearch_demo,
//                    RoutePOIActivity.class),

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTitle("3D地图Demo" + MapsInitializer.getVersion());
        ListAdapter adapter = new CustomArrayAdapter(
                this.getApplicationContext(), demos);
        setListAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
        if (demo.activityClass != null) {
            Log.i("MY", "demo!=null");
            startActivity(new Intent(this.getApplicationContext(),
                    demo.activityClass));
        }

    }
}