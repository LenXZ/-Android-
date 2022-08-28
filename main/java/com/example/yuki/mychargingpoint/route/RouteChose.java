package com.example.yuki.mychargingpoint.route;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
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
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.navi.view.RouteOverLay;
import com.example.yuki.mychargingpoint.R;
import com.autonavi.tbt.TrafficFacilityInfo;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RouteChose implements AMapNaviListener {
    /**
     * 导航对象(单例)
     */
    private AMapNavi mAMapNavi;
    private AMap mAmap;
    /**
     * 地图对象
     */
    private MapView mRouteMapView;
    /**
     * 保存当前算好的路线
     */
    private SparseArray<RouteOverLay> routeOverlays = new SparseArray<RouteOverLay>();
    private Context mBase;
    int strategyFlag1 = 0;
    int length =0;
    public void RouteChoseContextAMap(Context mcontext, AMap map){
        mAmap=map;
        mBase=mcontext;
        try {
            mAMapNavi = AMapNavi.getInstance(mcontext.getApplicationContext());
            mAMapNavi.addAMapNaviListener(this);
            Log.w("路径1000", "A1");
        } catch (Exception e) {
            Log.w("路径1000", "A3"+e);
        }
        strategyFlag1 = 0;
        try {
            strategyFlag1 = mAMapNavi.strategyConvert(false, false, false, false, false);
            Log.w("路径1000", "B1--"+strategyFlag1);
        } catch (Exception e) {
            Log.w("路径1111", "B2WRONG" + e);
            e.printStackTrace();
        }
    }
    public void RouteChose0(List<NaviLatLng> startList, List<NaviLatLng> endList, List<NaviLatLng> wayList) {

        {
            try {
                String carNumber = "粤FQK883";
                AMapCarInfo carInfo = new AMapCarInfo();
                //设置车牌
                carInfo.setCarNumber(carNumber);
                //设置车牌是否参与限行算路
                carInfo.setRestriction(false);
                mAMapNavi.setCarInfo(carInfo);
                mAMapNavi.calculateDriveRoute(startList, endList, wayList, strategyFlag1);
                Log.w("路径1000", "C1");
            } catch (Exception e) {
                Log.w("路径1000", "C2WRONG");
            }
        }
    }

    public int DriveRoute() {
//        mAmap.clear();
        Log.w("路径1000","路径路径路径"+length);
        return length;
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
    @Override
    public void onInitNaviSuccess() {
    }

    @Override
    public void onCalculateMultipleRoutesSuccess(int[] ints) {
        routeOverlays.clear();
        HashMap<Integer, AMapNaviPath> paths = mAMapNavi.getNaviPaths();
        for (int i = 0; i < ints.length; i++) {
            AMapNaviPath path = paths.get(ints[i]);
            if (path != null) {
                drawRoutes(ints[i], path);
                break;
            }
        }
    }

    @Override
    public void onCalculateRouteSuccess() {
        Log.i("1000", "P1");
        /**
         * 清空上次计算的路径列表。
         */
        routeOverlays.clear();
        AMapNaviPath path = mAMapNavi.getNaviPath();
        /**
         * 单路径不需要进行路径选择，直接传入－1即可
         */
        drawRoutes(-1, path);
    }

    @Override
    public void onCalculateRouteFailure(int arg0) {
        Toast.makeText(mBase, "计算路线失败，errorcode＝" + arg0, Toast.LENGTH_SHORT).show();
    }

    private void drawRoutes(int routeId, AMapNaviPath path) {
        Log.i("1000", "R");
        mAmap.moveCamera(CameraUpdateFactory.changeTilt(0));
        RouteOverLay routeOverLay = new RouteOverLay(mAmap, path, mBase);
        routeOverLay.setTrafficLine(false);
//        routeOverLay.addToMap();
        routeOverlays.put(routeId, routeOverLay);
        if (routeOverlays.size() == 1) {
            try{
                mAMapNavi.selectRouteId(routeOverlays.keyAt(0));
                length = (mAMapNavi.getNaviPath()).getAllLength();
                DriveRoute();
                routeOverLay.destroy();
                routeOverlays.clear();
                clearRoute();
            }catch (Exception e){
                Log.i("1000", "Q4"+e);
            }
            return;
        }
        routeOverLay.destroy();
        routeOverlays.clear();
        return;
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
}
