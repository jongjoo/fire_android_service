package com.example.lsm.firesensorproject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
    private String autologin_onoff = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //자동로그인 체크값 불러오기
        AutoLoginCheckLoad();
        if(autologin_onoff.equals("ON")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        //------------------------
        //   로그인 버튼 리스너
        //------------------------
        Button btn = (Button) this.findViewById(R.id.btn_login);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editID = (EditText) findViewById(R.id.edit_id);
                EditText editPW = (EditText) findViewById(R.id.edit_pw);

                String id = editID.getText().toString();
                String pw = editPW.getText().toString();

                if(id.equals("iot") && pw.equals("1234")) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(id.equals("")){
                    Toast.makeText(LoginActivity.this, "시리얼 넘버를 입력해주세요",
                            Toast.LENGTH_SHORT).show();
                }
                else if(pw.equals("")){
                    Toast.makeText(LoginActivity.this, "비밀번호를 입력해주세요",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "입력하신 정보가 맞지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    //-------------------------------------
    //   자동로그인 체크박스 클릭 이벤트
    //-------------------------------------
    public void onAutoLoginClicked(View v){
        boolean checked = ((CheckBox)v).isChecked();
        if(checked){
            AutoCheckBoxSaveOn();
        }
        else{
            AutoCheckBoxSaveOff();
        }
    }

    //자동로그인 상태값 ON으로 저장 (SharedPreference)
    private void AutoCheckBoxSaveOn() {
        SharedPreferences pref = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("OnOff", "ON");
        editor.apply();
    }

    //자동로그인 상태값 OFF로 저장 (SharedPreference)
    private void AutoCheckBoxSaveOff() {
        SharedPreferences pref = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("OnOff", "OFF");
        editor.apply();
    }

    //자동로그인 상태값 불러오기 (SharedPreference)
    private void AutoLoginCheckLoad() {
        SharedPreferences pref = getSharedPreferences("AutoLogin", Activity.MODE_PRIVATE);
        autologin_onoff = pref.getString("OnOff", "OFF");
    }
}