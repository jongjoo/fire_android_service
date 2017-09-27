package com.example.lsm.firesensorproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class AddPhoneNumberActivity extends AppCompatActivity {
    private ArrayList<MyData> arrList;
    public static MyAdapter adapter;
    private ListView listview;
    public static String name, phone_number;
    public static boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_number);

        if(flag)
            arrList = new ArrayList<MyData>();
        else
            arrList = getStringArrayPref(this, "NAME", "PHONENUMBER");

        adapter = new MyAdapter(this, arrList);
        listview = (ListView) findViewById(R.id.list_phone_number);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(AddPhoneNumberActivity.this,
                        arrList.get(position)+"",
                        Toast.LENGTH_SHORT).show();
            }
        });

        //-------------------------------
        //  연락처 가져오기 버튼 리스너
        //-------------------------------
        Button btn1 = (Button) this.findViewById(R.id.btn_get_phone_number);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onStop() {
        //액티비티 종료 시 ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);

        super.onStop();
    }
    @Override
    protected void onDestroy() {
        //액티비티 종료 시 ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);

        super.onDestroy();
    }
    @Override
    protected void onPause() {
        //액티비티 종료 시 ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);

        super.onPause();
    }

    //연락처 화면 열리고 연락처 선택 후 닫힐 때 실행됨
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            Cursor cursor = getContentResolver().query(data.getData(),
                    new String[]{
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,}, null, null, null);
            cursor.moveToFirst();

            name = cursor.getString(0);        //0은 이름을 얻어옵니다.
            phone_number = cursor.getString(1);   //1은 번호를 받아옵니다.

            //가져온 이름&번호 리스트에 추가
            arrList.add(new MyData(name, phone_number));

            //Listview 갱신
            adapter.notifyDataSetChanged();

            flag = false;

            cursor.close();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //폰 번호 "-" 문자열 파싱
    public String strimPhoneNumber(String str) {
        String temp = "";

        StringTokenizer token = new StringTokenizer(str, "-");
        for (int i = 1; token.hasMoreElements(); i++) {
            temp = temp + token.nextToken();
        }

        return temp;
    }

    private void setStringArrayPref(Context context, String key1, String key2, ArrayList<MyData> arr) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray json_name = new JSONArray();
        JSONArray json_phone_number = new JSONArray();

        for (int i = 0; i < arr.size(); i++) {
            json_name.put(arr.get(i).getName());
            json_phone_number.put(arr.get(i).getPhoneNumber());
        }
        if (!arr.isEmpty()) {
            editor.putString(key1, json_name.toString());
            editor.putString(key2, json_phone_number.toString());
        } else {
            editor.putString(key1, null);
            editor.putString(key2, null);
        }
        editor.apply();
    }

    private ArrayList<MyData> getStringArrayPref(Context context, String key1, String key2) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String str_name = prefs.getString(key1, null);
        String str_phone_number = prefs.getString(key2, null);
        ArrayList<MyData> urls = new ArrayList<MyData>();
        if (str_name != null) {
            try {
                JSONArray json_name = new JSONArray(str_name);
                JSONArray json_phone_number = new JSONArray(str_phone_number);
                for (int i = 0; i < json_name.length(); i++) {
                    String str1 = json_name.optString(i);
                    String str2 = json_phone_number.optString(i);
                    urls.add(new MyData(str1, str2));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }

    //뒤로가기 버튼 눌렀을 시 이전 액티비티로 전환
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, msg);
    }

    //커스텀 액션바
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
        View actionbar = inflater.inflate(R.layout.custom_actionbar_add_phone_number, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        //------------------------------
        //   뒤로가기 버튼 리스너
        //------------------------------
        ImageButton btn = (ImageButton) this.findViewById(R.id.btn_add_phone_number_back);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        return true;
    }
}