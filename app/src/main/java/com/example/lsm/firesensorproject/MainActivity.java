package com.example.lsm.firesensorproject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int DIALOG_GASVALVE_OPEN_MESSAGE = 1;
    private static final int DIALOG_GASVALVE_CLOSE_MESSAGE = 2;
    private static int flag = 0;
    private static String temp = "", humid = "", smoke = "", result;
    Context mContext = this;
    Intent intent;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------
        //   항상 떠있는 상단바 알림
        //------------------------------
        intent = new Intent(
                getApplicationContext(),//현재제어권자
                PushService.class); // 이동할 컴포넌트
        startService(intent); // 서비스 시작

        //------------------------------
        //  가스밸브 스위치 리스너
        //------------------------------
        Switch s = (Switch) this.findViewById(R.id.switch_gasvalve);
        s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (flag == 0) {
                        showDialog(DIALOG_GASVALVE_OPEN_MESSAGE);
                    }
                } else {
                    if (flag == 0) {
                        showDialog(DIALOG_GASVALVE_CLOSE_MESSAGE);
                    }
                }
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

        //-------------------------------------------
        //  가스밸브 스위치 상태 테스트 버튼 리스너
        //-------------------------------------------
        ImageButton btn2 = (ImageButton) this.findViewById(R.id.btn_test1);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GasvalveSwitchLoad();
            }
        });

        //-------------------------------------------
        //  환경설정 값 불러오기 테스트 버튼 리스너
        //-------------------------------------------
        ImageButton btn3 = (ImageButton) this.findViewById(R.id.btn_test2);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences setRefer = PreferenceManager.getDefaultSharedPreferences(mContext);
                if (setRefer.getBoolean("key_StatePushAlert", true))
                    Toast.makeText(MainActivity.this, "체크O", Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(MainActivity.this, "체크X", Toast.LENGTH_LONG).show();
            }
        });
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

        return true;
    }


    //-------------------------------------------------
    //  가스밸브 스위치 상태값 저장/불러오기 관련 메서드
    //-------------------------------------------------
    //가스밸브 스위치 상태값 ON으로 저장 (SharedPreference)
    private void GasvalveSwitchSaveOn() {
        SharedPreferences pref = getSharedPreferences("Gasvalve", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("OnOff", "ON");
        editor.apply();
    }

    //가스밸브 스위치 상태값 OFF로 저장 (SharedPreference)
    private void GasvalveSwitchSaveOff() {
        SharedPreferences pref = getSharedPreferences("Gasvalve", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("OnOff", "OFF");
        editor.apply();
    }

    //가스밸브 스위치 상태값 불러오기 (SharedPreference)
    private void GasvalveSwitchLoad() {
        SharedPreferences pref = getSharedPreferences("Gasvalve", Activity.MODE_PRIVATE);
        String onoff = pref.getString("OnOff", "OFF");
        Toast.makeText(MainActivity.this, "스위치 상태 : " + onoff, Toast.LENGTH_LONG).show();
    }

    //-------------------------------------------------
    //  가스밸브 스위치 열고 닫을때 나타나는 대화상자
    //-------------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_GASVALVE_OPEN_MESSAGE:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("")
                        .setMessage("가스밸브를 여시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //YES 눌렀을때 할 행동
                                        GasvalveSwitchSaveOn();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //NO 눌렀을때 할 행동
                                        Switch s = (Switch) findViewById(R.id.switch_gasvalve);
                                        flag = 1;
                                        s.setChecked(false);
                                        flag = 0;
                                    }
                                });
                AlertDialog alert1 = builder1.create();
                return alert1;
            case DIALOG_GASVALVE_CLOSE_MESSAGE:
                AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
                builder2.setTitle("")
                        .setMessage("가스밸브를 닫으시겠습니까?")
                        .setCancelable(false)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //YES 눌렀을때 할 행동
                                        GasvalveSwitchSaveOff();
                                    }
                                })
                        .setNegativeButton("NO",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        //NO 눌렀을때 할 행동
                                        Switch s = (Switch) findViewById(R.id.switch_gasvalve);
                                        flag = 1;
                                        s.setChecked(true);
                                        flag = 0;
                                    }
                                });
                AlertDialog alert2 = builder2.create();
                return alert2;
        }
        return null;
    }
}
