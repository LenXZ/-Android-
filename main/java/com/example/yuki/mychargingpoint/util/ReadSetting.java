package com.example.yuki.mychargingpoint.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

/**
 * Created by Yuki on 2017/6/7.
 */

public class ReadSetting {

    private Context othercontext;
    private SharedPreferences sp;
    public String ReadSetting(){
                try {
                    othercontext = othercontext.createPackageContext("com.example.yuki.mychargingpoint", Context.CONTEXT_IGNORE_SECURITY);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                //根据Context取得对应的SharedPreferences
                sp = othercontext.getSharedPreferences("mysp", Context.MODE_WORLD_READABLE);
                String city = sp.getString("city", "");
                String carnumber = sp.getString("carnumber", "");
                return carnumber;
    }
}
