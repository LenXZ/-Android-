package com.example.yuki.mychargingpoint.util;

/**
 * Created by Yuki on 2017/6/7.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.example.yuki.mychargingpoint.R;

import java.util.Map;

public class DialogFragmentSetting extends DialogFragment {
    private Context mContext;
    private EditText editcity;
    private EditText editcarnumber;
    private String strcity;
    private String strcarnumber;
    private SharedHelper sh;
    public interface LoginInputListener {
        void onLoginInputComplete(String username, String password);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_setting_dialog, null);
//        mContext = BasicMapActivity.instance.getApplicationContext();
        sh = new SharedHelper(mContext);
        editcity = (EditText) view.findViewById(R.id.editcity);
        editcarnumber = (EditText) view.findViewById(R.id.car_number);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("保存",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                strcity = editcity.getText().toString();
                                strcarnumber = editcarnumber.getText().toString();
                                sh.save(strcity,strcarnumber);
//                                LoginInputListener listener = (LoginInputListener) getActivity();
//                                listener.onLoginInputComplete(editcity
//                                        .getText().toString(), editcarnumber
//                                        .getText().toString());
                            }
                        }).setNegativeButton("取消", null);
        return builder.create();
    }
    public DialogFragmentSetting(){
    }
    public void DialogFragmentSetting(Context context){
        this.mContext = context;
    }
    @Override
    public void onStart() {
        super.onStart();
        Map<String,String> data = sh.read();
        editcity.setText(data.get("city"));
        editcarnumber.setText(data.get("carnumber"));
    }
}