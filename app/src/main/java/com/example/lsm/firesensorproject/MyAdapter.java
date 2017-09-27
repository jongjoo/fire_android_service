package com.example.lsm.firesensorproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.lsm.firesensorproject.AddPhoneNumberActivity.adapter;

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
                if (pos >= 0 && pos < arrData.size()) {
                    arrData.remove(pos);
                    adapter.notifyDataSetChanged();
                }
            }
        });
        return convertView;
    }
}