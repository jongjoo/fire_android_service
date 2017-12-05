package com.example.lsm.firesensorproject;

import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_phone_number);

        arrList = new ArrayList<MyData>();

        //Json배열에 있는 데이터 ArrayList에 불러오기
        arrList = getStringArrayPref(this, "NAME", "PHONENUMBER");

        adapter = new MyAdapter(this, arrList);
        listview = (ListView) findViewById(R.id.list_phone_number);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Toast.makeText(AddPhoneNumberActivity.this,
                        arrList.get(position) + "",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        //ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        //ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //ArrayList에 있는 데이터 JSON 배열에 저장
        setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);
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

    //ArryList에 있는 데이터 JSON 배열에 저장
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

    //JSON 배열에 저장되어 있는 데이터 ArrayList에 불러오기
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

    //----------------------------------------------
    //  연락처 삭제 버튼 누를 때 나타나는 대화상자
    //----------------------------------------------
    @Override
    protected Dialog onCreateDialog(int id) {
        final int position = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("정말 삭제하시겠습니까?")
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //YES 눌렀을때 할 행동
                                if (position >= 0 && position < adapter.getArrData().size()) {
                                    adapter.getArrData().remove(position);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //NO 눌렀을때 할 행동
                            }
                        });
        AlertDialog alert = builder.create();
        return alert;
    }

    //------------------------
    //   문자 메시지 보내기
    //------------------------
    private void sendSMS() {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        String content = "[긴급]\n\u0023화재발생\n주소:경기도 용인시 기흥구 강남로40\n" +
                "온도: 80℃\n습도: 5%\n화재발생시각: P.M. 02:35";
        ArrayList<MyData> arr;

        SmsManager sms = SmsManager.getDefault();

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0);

        registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "문자 메시지가 전송되었습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //119로 문자 전송
        sms.sendTextMessage("119", null, content, sentPI, deliveredPI);

        //추가로 등록한 전화번호로 문자 전송
        arr = getStringArrayPref(AddPhoneNumberActivity.this, "NAME", "PHONENUMBER");
        for(int i=0; i < arr.size(); i++){
            sms.sendTextMessage(arr.get(i).getPhoneNumber(), null, content, sentPI, deliveredPI);
        }
    }

    public class MyAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<MyData> arrData;
        private LayoutInflater inflater;

        public MyAdapter(Context c, ArrayList<MyData> arr) {
            this.context = c;
            this.arrData = arr;
            inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        public int getCount() {
            return arrData.size();
        }
        public Object getItem(int position) {
            return arrData.get(position).getName();
        }
        public long getItemId(int position) {
            return position;
        }
        public ArrayList<MyData> getArrData(){
            return arrData;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;

            if(convertView == null){
                convertView = inflater.inflate(R.layout.list_item_phone_number, parent, false);
            }

            TextView tv1 = (TextView)convertView.findViewById(R.id.tv_name);
            tv1.setText(arrData.get(position).getName());

            TextView tv2 = (TextView)convertView.findViewById(R.id.tv_phone_number);
            tv2.setText(arrData.get(position).getPhoneNumber());

            //----------------------------
            //  연락처 삭제 버튼 리스너
            //----------------------------
            ImageButton btn = (ImageButton) convertView.findViewById(R.id.btn_delete_phone_number);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDialog(pos);
                }
            });
            return convertView;
        }
    }

    public class MyData {
        private String name;
        private String phone_number;

        public MyData(String name, String phone_number) {
            this.name = name;
            this.phone_number = phone_number;
        }

        public String getName() {
            return name;
        }

        public String getPhoneNumber() {
            return phone_number;
        }
    }


    //뒤로가기 버튼 눌렀을 시 이전 액티비티로 전환
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        } else if( keyCode == KeyEvent.KEYCODE_MENU) {
            setStringArrayPref(this, "NAME", "PHONENUMBER", arrList);
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
        LayoutInflater inflater1 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater1.inflate(R.layout.custom_actionbar_add_phone_number, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar) actionbar.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        //옵션 메뉴 생성
        MenuInflater inflater2 = getMenuInflater();
        inflater2.inflate(R.menu.menu_add_phone_number,menu);

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

    //옵션 메뉴 선택 이벤트 처리
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            //추가 눌렀을 시
            case R.id.item_get_phone_number:
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setData(ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(intent, 0);
                return true;
            //삭제 눌렀을 시
            case R.id.item_del_phone_number:
                return true;
            //문자보내기 눌렀을 시
            case R.id.item_send_sms:
                sendSMS();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}