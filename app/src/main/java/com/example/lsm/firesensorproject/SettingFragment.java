package com.example.lsm.firesensorproject;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.TextView;

import static com.example.lsm.firesensorproject.PushService.noti_id;
import static com.example.lsm.firesensorproject.PushService.noti_manager;
import static com.example.lsm.firesensorproject.PushService.push_handler;
import static com.example.lsm.firesensorproject.PushService.push_thread;


public class SettingFragment extends PreferenceFragment {
    EditText editTime;
    TextView textAddress1, textAddress2;
    private int num;
    String gasvalve_time = "10";
    String normal_address, detail_address;
    public static boolean push_flag = true;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.activity_setting);

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

        //--------------------------------
        //  고정 상단바 알림 설정 리스너
        //--------------------------------
        Preference pref3 = (Preference) findPreference("key_StatePushAlert");
        pref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                SharedPreferences setRefer = PreferenceManager.getDefaultSharedPreferences(getActivity());
                if (setRefer.getBoolean("key_StatePushAlert", true)) {
                    push_flag = true;
                    push_thread = new PushThread(push_handler);
                    push_thread.start();
                } else {
                    push_flag = false;
                    push_thread.stopForever();
                    noti_manager.cancel(noti_id);
                }
                return false;
            }
        });

        //---------------------
        //   주소 설정 리스너
        //---------------------
        Preference pref4 = (Preference) findPreference("key_SetAddress");
        pref4.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), SetAddressActivity.class);
                startActivity(intent);
                return false;
            }
        });

        //---------------------
        //   주소 확인 리스너
        //---------------------
        Preference pref5 = (Preference) findPreference("key_ShowAddress");
        pref5.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ShowAddressDialog();
                return false;
            }
        });

        //---------------------
        //   로그아웃 리스너
        //---------------------
        Preference pref6 = (Preference) findPreference("key_Logout");
        pref6.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                AutoCheckBoxSaveOff();
                return false;
            }
        });
    }

    //자동로그인 상태값 OFF로 저장 (SharedPreference)
    private void AutoCheckBoxSaveOff() {
        SharedPreferences pref = getActivity().getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("OnOff", "OFF");
        editor.apply();
    }

    //입력한 기본 주소 불러오기 (SharedPreference)
    private void NormalAddressLoad() {
        SharedPreferences pref = getActivity().getSharedPreferences("NormalAddress", Activity.MODE_PRIVATE);
        normal_address = pref.getString("Address", "입력된 정보 없음");
    }

    //입력한 상세 주소 불러오기 (SharedPreference)
    private void DetailAddressLoad() {
        SharedPreferences pref = getActivity().getSharedPreferences("DetailAddress", Activity.MODE_PRIVATE);
        detail_address = pref.getString("Address", "입력된 정보 없음");
    }

    //------------------------
    //   주소 확인 대화상자
    //------------------------
    public void ShowAddressDialog() {
        //Dialog에서 보여줄 입력화면 View 객체 생성 작업
        //Layout xml 리소스 파일을 View 객체로 부풀려 주는(inflate) LayoutInflater 객체 생성
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //Dialog의 listener에서 사용하기 위해 final로 참조변수 선언
        final View dialogView = inflater.inflate(R.layout.dialog_showaddress, null);

        textAddress1 = (TextView) dialogView.findViewById(R.id.show_address1);
        textAddress2 = (TextView) dialogView.findViewById(R.id.show_address2);

        //멤버의 세부내역 입력 Dialog 생성 및 보이기
        AlertDialog.Builder buider = new AlertDialog.Builder(getActivity()); //AlertDialog.Builder 객체 생성

        buider.setTitle("주소 확인"); //Dialog 제목
        buider.setView(dialogView); //위에서 inflater가 만든 dialogView 객체 세팅 (Customize)
        buider.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            //Dialog에 "확인"이라는 타이틀의 버튼을 설정
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //설정한 값으로 AlertDialog 객체 생성
        AlertDialog dialog = buider.create();

        //Dialog의 바깥쪽을 터치했을 때 Dialog를 없앨지 설정
        dialog.setCanceledOnTouchOutside(false);

        //SharedPreference에 저장된 주소 값 불러와서 적용시키기
        NormalAddressLoad();
        DetailAddressLoad();
        textAddress1.setText(normal_address);  //기본 주소
        textAddress2.setText(detail_address);  //상세 주소

        //Dialog 보이기
        dialog.show();
    }
}