package com.example.yuki.mychargingpoint.util;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.navi.model.NaviLatLng;
import com.example.yuki.mychargingpoint.R;

/**
 * Created by Yuki on 2017/6/2.
 */

public class AddMarker {
    public Marker marker3[] = new Marker[100];
    public void addMarkersToMap( AMap aMap){
        /**控制台 | 高德开放平台 | 高德地图API  http://lbs.amap.com/console/show/picker  进行坐标拾取,经纬度位置调换*/
        marker3[1] = aMap.addMarker(new MarkerOptions().title("充电点01").snippet("白城大地宏大加油加气站").position(
                Constants.chargingpointbaicheng01).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[2] = aMap.addMarker(new MarkerOptions().title("充电点02").snippet("天伦燃气加油加气站").position(
                Constants.chargingpointbaicheng02).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[3] = aMap.addMarker(new MarkerOptions().title("充电点03").snippet("中吉大地加油站(民主西路)").position(
                Constants.chargingpointbaicheng03).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[4] = aMap.addMarker(new MarkerOptions().title("充电点04").snippet("银通加油站").position(
                Constants.chargingpointbaicheng04).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[5] = aMap.addMarker(new MarkerOptions().title("充电点05").snippet("中国石油福鑫加油站").position(
                Constants.chargingpointbaicheng05).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[6] = aMap.addMarker(new MarkerOptions().title("充电点06").snippet("中国石油加油站").position(
                Constants.chargingpointbaicheng06).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[7] = aMap.addMarker(new MarkerOptions().title("充电点07").snippet("中国石油加油站").position(
                Constants.chargingpointbaicheng07).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[8] = aMap.addMarker(new MarkerOptions().title("充电点08").snippet("中国石油灯塔加油站").position(
                Constants.chargingpointbaicheng08).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[9] = aMap.addMarker(new MarkerOptions().title("充电点09").snippet("中国石油(昆仑润滑油定制体验店)").position(
                Constants.chargingpointbaicheng09).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[10] = aMap.addMarker(new MarkerOptions().title("充电点10").snippet("中国石油加油站(民主东路)").position(
                Constants.chargingpointbaicheng10).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
        marker3[11] = aMap.addMarker(new MarkerOptions().title("充电点11").snippet("中国石油加油站").position(
                Constants.chargingpointbaicheng11).icon(
                BitmapDescriptorFactory.fromResource(R.mipmap.pointmarker)));
    }
    public void MarkerRemove(){
        for(int i=0;i<=11;i++){
            try {
                marker3[i].remove();
            }catch (Exception e){

            }
            try {
                marker3[i].destroy();
            }catch (Exception e){

            }
        }

    }
    public void MarkerRemove(AMap aMap){
        aMap.clear();
    }
    public NaviLatLng getNaviLatLng(int i){
        NaviLatLng wayLatlng[]=new NaviLatLng[100];
        wayLatlng[1] = new NaviLatLng(Constants.chargingpointbaicheng01.latitude,Constants.chargingpointbaicheng01.longitude);
        wayLatlng[2] = new NaviLatLng(Constants.chargingpointbaicheng02.latitude,Constants.chargingpointbaicheng02.longitude);
        wayLatlng[3] = new NaviLatLng(Constants.chargingpointbaicheng03.latitude,Constants.chargingpointbaicheng03.longitude);
        wayLatlng[4] = new NaviLatLng(Constants.chargingpointbaicheng04.latitude,Constants.chargingpointbaicheng04.longitude);
        wayLatlng[5] = new NaviLatLng(Constants.chargingpointbaicheng05.latitude,Constants.chargingpointbaicheng05.longitude);
        wayLatlng[6] = new NaviLatLng(Constants.chargingpointbaicheng06.latitude,Constants.chargingpointbaicheng06.longitude);
        wayLatlng[7] = new NaviLatLng(Constants.chargingpointbaicheng07.latitude,Constants.chargingpointbaicheng07.longitude);
        wayLatlng[8] = new NaviLatLng(Constants.chargingpointbaicheng08.latitude,Constants.chargingpointbaicheng08.longitude);
        wayLatlng[9] = new NaviLatLng(Constants.chargingpointbaicheng09.latitude,Constants.chargingpointbaicheng09.longitude);
        wayLatlng[10] = new NaviLatLng(Constants.chargingpointbaicheng10.latitude,Constants.chargingpointbaicheng10.longitude);
        wayLatlng[11] = new NaviLatLng(Constants.chargingpointbaicheng11.latitude,Constants.chargingpointbaicheng11.longitude);
        wayLatlng[12] = new NaviLatLng(Constants.chargingpointbaicheng12.latitude,Constants.chargingpointbaicheng12.longitude);
        return wayLatlng[i];
    }
}
