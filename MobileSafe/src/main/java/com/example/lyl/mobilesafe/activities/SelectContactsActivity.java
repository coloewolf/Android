package com.example.lyl.mobilesafe.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ContactsInfoUtils;
import com.example.lyl.mobilesafe.javabean.ContactsInfo;

import java.util.List;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class SelectContactsActivity extends AppCompatActivity{

    private ListView lv_contacts;
    private List<ContactsInfo> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_contacts);
        list = ContactsInfoUtils.getContactsInfo(this);
        lv_contacts = (ListView) findViewById(R.id.lv_contacts);
        lv_contacts.setAdapter(new MyAdapter());
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String phoneNumber = list.get(position).getPhoneNumber();
                Intent data = new Intent();
                data.putExtra("phoneNumber",phoneNumber);
                setResult(0,data);
                finish();
            }
        });
    }

    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(SelectContactsActivity.this,R.layout.items_contacts,null);
            TextView tv_contactName = (TextView) view.findViewById(R.id.tv_contactName);
            TextView tv_phoneNumber = (TextView) view.findViewById(R.id.tv_phoneNumber);
            tv_contactName.setText(list.get(position).getName());
            tv_phoneNumber.setText(list.get(position).getPhoneNumber());
            return view;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
    }
}
