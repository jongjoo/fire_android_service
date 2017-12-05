package com.example.lsm.firesensorproject;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DataReceiver extends BroadcastReceiver {
    public static String receive_data="";

    @Override
    public void onReceive(Context context, Intent intent) {
        String name = intent.getAction();

        /***
        if(name.equals("com.example.lsm.firesensorproject.SEND_BROAD_CAST")){
            receive_data = intent.getStringExtra("sendData");
            Toast.makeText(context, "수신 성공, line: "+receive_data, Toast.LENGTH_LONG).show();
        }
         ***/
    }
}
