/**
 * 
 */
package com.example.yuki.mychargingpoint.location;

import android.text.TextUtils;

import com.amap.api.location.AMapLocation;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.text.Html;
import android.text.Spanned;

import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviStep;

import java.text.DecimalFormat;
import java.util.List;

/**
 * 辅助工具类
 * @创建时间： 2015年11月24日 上午11:46:50
 * @项目名称： AMapLocationDemo2.x
 * @author hongming.wang
 * @文件名称: Utils.java
 * @类型名称: Utils
 */
public class Utils {
	/**
	 *  开始定位
	 */
	public final static int MSG_LOCATION_START = 0;
	/**
	 * 定位完成
	 */
	public final static int MSG_LOCATION_FINISH = 1;
	/**
	 * 停止定位
	 */
	public final static int MSG_LOCATION_STOP= 2;
	
	public final static String KEY_URL = "URL";
	public final static String URL_H5LOCATION = "file:///android_asset/location.html";
	private static DecimalFormat fnum = new DecimalFormat("##0.0");
	public static final int AVOID_CONGESTION = 4;  // 躲避拥堵
	public static final int AVOID_COST = 5;  // 避免收费
	public static final int AVOID_HIGHSPEED = 6; //不走高速
	public static final int PRIORITY_HIGHSPEED = 7; //高速优先

	public static final int START_ACTIVITY_REQUEST_CODE = 1;
	public static final int ACTIVITY_RESULT_CODE = 2;

	public static final String INTENT_NAME_AVOID_CONGESTION = "AVOID_CONGESTION";
	public static final String INTENT_NAME_AVOID_COST = "AVOID_COST";
	public static final String INTENT_NAME_AVOID_HIGHSPEED = "AVOID_HIGHSPEED";
	public static final String INTENT_NAME_PRIORITY_HIGHSPEED = "PRIORITY_HIGHSPEED";


	public static String getFriendlyTime(int s) {
		String timeDes = "";
		int h = s / 3600;
		if (h > 0) {
			timeDes += h + "小时";
		}
		int min = (int) (s % 3600) / 60;
		if (min > 0) {
			timeDes += min + "分";
		}
		return timeDes;
	}

	public static String getFriendlyDistance(int m) {
		if (m < 1000) {
			return m + "米";
		}
		float dis = m / 1000f;
		String disDes = fnum.format(dis) + "公里";
		return disDes;
	}

	public static Spanned getRouteOverView(AMapNaviPath path) {
		String routeOverView = "";
		if (path == null) {
			Html.fromHtml(routeOverView);
		}

		int cost = path.getTollCost();
		if (cost > 0) {
			routeOverView += "过路费约<font color=\"red\" >" + cost + "</font>元";
		}
		int trafficLightNumber = getTrafficNumber(path);
		if (trafficLightNumber > 0) {
			routeOverView += "红绿灯" + trafficLightNumber + "个";
		}
		return Html.fromHtml(routeOverView);
	}

	public static int getTrafficNumber(AMapNaviPath path) {
		int trafficLightNumber = 0;
		if (path == null) {
			return trafficLightNumber;
		}
		List<AMapNaviStep> steps = path.getSteps();
		for (AMapNaviStep step : steps) {
			trafficLightNumber += step.getTrafficLightNumber();
		}
		return trafficLightNumber;
	}
	/**
	 * 根据定位结果返回定位信息的字符串
	 * @param loc
	 * @return
	 */
	public synchronized static String getLocationStr(AMapLocation location){
		if(null == location){
			return null;
		}
		StringBuffer sb = new StringBuffer();
		//errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
		if(location.getErrorCode() == 0){
			sb.append("定位成功" + "\n");
			sb.append("定位类型: " + location.getLocationType() + "\n");
			sb.append("经    度    : " + location.getLongitude() + "\n");
			sb.append("纬    度    : " + location.getLatitude() + "\n");
			sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
			sb.append("提供者    : " + location.getProvider() + "\n");
			
			sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
			sb.append("角    度    : " + location.getBearing() + "\n");
			// 获取当前提供定位服务的卫星个数
			sb.append("星    数    : " + location.getSatellites() + "\n");
			sb.append("国    家    : " + location.getCountry() + "\n");
			sb.append("省            : " + location.getProvince() + "\n");
			sb.append("市            : " + location.getCity() + "\n");
			sb.append("城市编码 : " + location.getCityCode() + "\n");
			sb.append("区            : " + location.getDistrict() + "\n");
			sb.append("区域 码   : " + location.getAdCode() + "\n");
			sb.append("地    址    : " + location.getAddress() + "\n");
			sb.append("兴趣点    : " + location.getPoiName() + "\n");
			//定位完成的时间
			sb.append("定位时间: " + formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
		} else {
			//定位失败
			sb.append("定位失败" + "\n");
			sb.append("错误码:" + location.getErrorCode() + "\n");
			sb.append("错误信息:" + location.getErrorInfo() + "\n");
			sb.append("错误描述:" + location.getLocationDetail() + "\n");
		}
		//定位之后的回调时间
		sb.append("回调时间: " + formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
		return sb.toString();
	}
	
	private static SimpleDateFormat sdf = null;
	public synchronized static String formatUTC(long l, String strPattern) {
		if (TextUtils.isEmpty(strPattern)) {
			strPattern = "yyyy-MM-dd HH:mm:ss";
		}
		if (sdf == null) {
			try {
				sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
			} catch (Throwable e) {
			}
		} else {
			sdf.applyPattern(strPattern);
		}
		return sdf == null ? "NULL" : sdf.format(l);
	}
}
