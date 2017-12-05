package com.example.lsm.firesensorproject;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataService extends Service {
    Thread data_thread;
    public static String str_state = "0";

    @Override
    public IBinder onBind(Intent intent) {
        // Service 객체와 (화면단 Activity 사이에서)
        // 통신(데이터를 주고받을) 때 사용하는 메서드
        // 데이터를 전달할 필요가 없으면 return null;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

        data_thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        String line = null;
                        try {
                            URL url = new URL("http://211.253.11.58:3000/kang_down");

                            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                            conn.setRequestMethod("GET");
                            InputStream is = conn.getInputStream();

                            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                            line = reader.readLine();

                            /*** 브로드 캐스트 리시버 사용
                             Intent sendIntent = new Intent("com.example.lsm.firesensorproject.SEND_BROAD_CAST");
                             sendIntent.putExtra("sendData", line);
                             sendBroadcast(sendIntent);
                             ***/

                            ParsingJsonData(line);
                        } catch (IOException e) {
                            e.printStackTrace();
                            line = e.toString();
                        }
                        sleep(2000);      //2초마다 데이터 갱신
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        data_thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 서비스가 호출될 때마다 실행

        //data_thread.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
    }

    //Json Data 파싱
    public void ParsingJsonData(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            JSONArray datas = obj.getJSONArray("data");
            JSONObject data = datas.getJSONObject(0);

            //JSON 객체 저장
            DataJSONSave(result);

            //데이터 센서 값 저장
            DataTempSave(data.getString("temp"));
            DataMoisSave(data.getString("mois"));
            DataGasSave(data.getString("gas"));
            DataMoveSave(data.getString("move"));

            //현재 상태 값 저장
            DataThreatSave(data.getString("threat"));
            str_state = DataThreatLoad();
            //상태 값에 따라 state 문구 변경
            if(str_state.equals("0")){
                str_state = "정상";
            } else if(str_state.equals("1")){
                str_state = "위험";
            } else if(str_state.equals("2")){
                str_state = "화재";
            } else {
                str_state = "Error";
            }
        } catch (JSONException o) {
            o.printStackTrace();
            result = o.toString();
        }
    }


    //데이터 값 JSON 객체로 저장 (SharedPreference)
    private void DataJSONSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataJSON", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }

    //상태 데이터 값 저장 (SharedPreference)
    private void DataThreatSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataThreat", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }
    //온도 데이터 값 저장 (SharedPreference)
    private void DataTempSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataTemp", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }
    //습도 데이터 값 저장 (SharedPreference)
    private void DataMoisSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataMois", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }
    //가스 데이터 값 저장 (SharedPreference)
    private void DataGasSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataGas", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }
    //모션인식 데이터 값 저장 (SharedPreference)
    private void DataMoveSave(String data) {
        SharedPreferences pref = getSharedPreferences("DataMove", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("Data", data);
        editor.apply();
    }

    //상태 데이터 값 불러오기 (SharedPreference)
    private String DataThreatLoad() {
        SharedPreferences pref = getSharedPreferences("DataThreat", Activity.MODE_PRIVATE);
        return pref.getString("Data", "0");
    }
}