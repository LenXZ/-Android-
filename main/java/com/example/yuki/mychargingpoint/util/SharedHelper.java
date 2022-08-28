package com.example.yuki.mychargingpoint.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.yuki.mychargingpoint.basic.BasicMapActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yuki on 2017/6/7.
 */
public class SharedHelper {

    private Context mContext;

    public SharedHelper() {}

    public SharedHelper(Context mContext) {
        this.mContext = mContext;
    }

    //定义一个保存数据的方法
    public void save(String city, String carnumber) {
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Activity.MODE_PRIVATE);//不可被其他程序读写
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("city", city);
        editor.putString("carnumber", carnumber);
        editor.commit();
        Toast.makeText(mContext, "信息已写入SharedPreference中", Toast.LENGTH_SHORT).show();
    }

    //定义一个读取SP文件的方法
    public Map<String, String> read() {
        Map<String, String> data = new HashMap<String, String>();
        SharedPreferences sp = mContext.getSharedPreferences("mysp", Activity.MODE_PRIVATE);//不可被其他程序读写
        data.put("city", sp.getString("city", ""));
        data.put("carnumber", sp.getString("carnumber", ""));
        return data;
    }
}