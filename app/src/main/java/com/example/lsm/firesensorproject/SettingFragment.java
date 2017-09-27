package com.example.lsm.firesensorproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static com.example.lsm.firesensorproject.PushService.handler;
import static com.example.lsm.firesensorproject.PushService.noti_id;
import static com.example.lsm.firesensorproject.PushService.noti_manager;
import static com.example.lsm.firesensorproject.PushService.thread;

public class SettingFragment extends PreferenceFragment {
    TextView tv;
    private int num;
    public static boolean push_flag = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

        //--------------------------------------------
        //  가스밸브 자동차단 모드 시간 설정 리스너
        //--------------------------------------------
        Preference pref1 = (Preference) findPreference("key_GasvalveTimeSet");
        pref1.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShowDialog();
                return false;
            }
        });

        //-----------------------------------------
        //  자동 SMS 발송 추가 연락처 등록 리스너
        //-----------------------------------------
        Preference pref2 = (Preference) findPreference("key_AddPhoneNumber");
        pref2.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), AddPhoneNumberActivity.class);
                startActivity(intent);
                return false;
            }
        });

        //---------------------------
        //  고정 상단바 알림 리스너
        //---------------------------
        Preference pref3 = (Preference) findPreference("key_StatePushAlert");
        pref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences setRefer = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (setRefer.getBoolean("key_StatePushAlert", true)) {
                    push_flag = true;
                    thread = new PushThread(handler);
                    thread.start();
                } else {
                    push_flag = false;
                    thread.stopForever();
                    noti_manager.cancel(noti_id);
                }
                return false;
            }
        });
    }

    public void ShowDialog() {
        //Dialog에서 보여줄 입력화면 View 객체 생성 작업

        //Layout xml 리소스 파일을 View 객체로 부풀려 주는(inflate) LayoutInflater 객체 생성
        LayoutInflater inflater = getActivity().getLayoutInflater();


        //res폴더>>layout폴더>>dialog_timeset.xml 레이아웃 리소스 파일로 View 객체 생성

        //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언

        final View dialogView = inflater.inflate(R.layout.dialog_timeset, null);


        //멤버의 세부내역 입력 Dialog 생성 및 보이기

        AlertDialog.Builder buider = new AlertDialog.Builder(getActivity()); //AlertDialog.Builder 객체 생성

        buider.setTitle("시간 설정"); //Dialog 제목
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            //Dialog에 "확인"이라는 타이틀의 버튼을 설정
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });
        buider.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            //Dialog에 "취소"라는 타이틀의 버튼을 설정
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // Dialog를 종료하는 작업
            }

        });

        //설정한 값으로 AlertDialog 객체 생성
        AlertDialog dialog = buider.create();

        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);//없어지지 않도록 설정

        //Dialog 보이기
        dialog.show();

        tv = (TextView) dialogView.findViewById(R.id.tv_time);

        //----------------------
        //   증가 버튼 리스너
        //----------------------
        Button btn1 = (Button) dialogView.findViewById(R.id.btn_increase);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = Integer.parseInt(tv.getText().toString());
                if (num < 120)
                    num++;
                tv.setText(String.valueOf(num));
            }
        });

        //----------------------
        //   감소 버튼 리스너
        //----------------------
        Button btn2 = (Button) dialogView.findViewById(R.id.btn_decrease);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                num = Integer.parseInt(tv.getText().toString());
                if (num > 0)
                    num--;
                tv.setText(String.valueOf(num));
            }
        });
    }
}