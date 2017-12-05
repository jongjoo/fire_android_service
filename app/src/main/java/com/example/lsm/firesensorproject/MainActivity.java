package com.example.lsm.firesensorproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private Context mContext = this;
    private Intent intent_data, intent_push;
    private TextView tv_temp, tv_mois, tv_move_sense, tv_state;
    private ImageView imageView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_temp = (TextView) findViewById(R.id.tv_temp);
        tv_mois = (TextView) findViewById(R.id.tv_mois);
        tv_move_sense = (TextView) findViewById(R.id.tv_move_sense);
        tv_state = (TextView) findViewById(R.id.tv_state);
        imageView = (ImageView) findViewById(R.id.imageView);

        //----------------------------------
        //  데이터 센서값 받아오는 Service
        //----------------------------------
        intent_data = new Intent(
                getApplicationContext(),//현재제어권자
                DataService.class); // 이동할 컴포넌트
        startService(intent_data); // 서비스 시작

        //------------------------------------
        //   항상 떠있는 상단바 알림 Service
        //------------------------------------
        intent_push = new Intent(
                getApplicationContext(),//현재제어권자
                PushService.class); // 이동할 컴포넌트
        startService(intent_push); // 서비스 시작


        //데이터 받아와서 3초마다 메인UI 갱신
        new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        //   AsyncTask 시작
                        new DataGetTask().execute("http://211.253.11.58:3000/kang_down");
                        sleep(2000);      //2초마다 데이터 갱신
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //-----------------------------------------------------------
    //  서버에서 온.습도 받아와서 파싱 후 UI 변경하는 AsyncTask
    //-----------------------------------------------------------
    private class DataGetTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strs) {
            return DataJSONLoad();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray datas = obj.getJSONArray("data");
                JSONObject data = datas.getJSONObject(0);

                tv_temp.setText(data.getString("temp"));
                tv_mois.setText(data.getString("mois"));

                //움직임 센서 값에 따라 메인UI변경
                if (data.getString("move").equals("0")) {
                    tv_move_sense.setText("미감지");
                } else if ((data.getString("move").equals("1"))) {
                    tv_move_sense.setText("감지");
                }

                //현재 상태에 따라 메인UI변경
                if (data.getString("threat").equals("0")) {
                    imageView.setBackgroundResource(R.drawable.state_normal);
                    tv_state.setText(R.string.state_normal);
                } else if (data.getString("threat").equals("1")) {
                    imageView.setBackgroundResource(R.drawable.state_warning);
                    tv_state.setText(R.string.state_warning);
                } else if (data.getString("threat").equals("2")) {
                    imageView.setBackgroundResource(R.drawable.state_danger);
                    tv_state.setText(R.string.state_danger);
                }
            } catch (JSONException o) {
                o.printStackTrace();
                result = o.toString();

                Toast.makeText(getApplicationContext(), " 서버로부터 데이터를 받아올 수 없습니다.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void SleepTime(int delayTime) {
        long saveTime = System.currentTimeMillis();
        long currTime = 0;
        while (currTime - saveTime < delayTime) {
            currTime = System.currentTimeMillis();
        }
    }

    //데이터 값 JSON객체로 불러오기 (SharedPreference)
    private String DataJSONLoad() {
        SharedPreferences pref = getSharedPreferences("DataJSON", Activity.MODE_PRIVATE);
        return pref.getString("Data", "null");
    }


    //--------------------------
    //     커스텀 액션바
    //--------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.

        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar_main, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        //설정 버튼 리스너
        ImageButton btn = (ImageButton) this.findViewById(R.id.btn_setting);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        //------------------------------
        //   실시간 영상보기 버튼 리스너
        //------------------------------
        ImageButton btn1 = (ImageButton) this.findViewById(R.id.btn_camera);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CameraActivity.class);
                startActivity(intent);
            }
        });

        return true;
    }
}
