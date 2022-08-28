package com.example.yuki.mychargingpoint.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.yuki.mychargingpoint.R;
import com.example.yuki.mychargingpoint.util.SharedHelper;

import java.util.Map;

/**
 * Created by Yuki on 2017/6/7.
 */
public class SettingActivity extends AppCompatActivity {

    private EditText editcity;
    private EditText editcarnumber;
    private Button btnlogin;
    private String strcity;
    private String strcarnumber;
    private SharedHelper sh;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_activity);
        sh = new SharedHelper();
        bindViews();
    }

    private void bindViews() {
        editcity = (EditText)findViewById(R.id.editcity);
        editcarnumber = (EditText)findViewById(R.id.car_number);
        btnlogin = (Button)findViewById(R.id.btnlogin);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strcity = editcity.getText().toString();
                strcarnumber = editcarnumber.getText().toString();
                sh.save(strcity,strcarnumber);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Map<String,String> data = sh.read();
        editcity.setText(data.get("city"));
        editcarnumber.setText(data.get("carnumber"));
    }
}