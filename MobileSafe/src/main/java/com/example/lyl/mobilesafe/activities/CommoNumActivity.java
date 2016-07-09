package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.dao.CommonumDao;

/**
 * Created by lyl on 2016/7/2.
 *
 */
public class CommoNumActivity extends AppCompatActivity{

    private ExpandableListView elv;
    private CommonumDao mDao;
    private Context mContext;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonum);
        mContext = CommoNumActivity.this;
        db = SQLiteDatabase.openDatabase(mContext.getFilesDir() + "/commonnum.db", null, SQLiteDatabase.OPEN_READONLY);
        mDao = new CommonumDao(db);
        elv = (ExpandableListView) findViewById(R.id.elv_);
        elv.setAdapter(new MyExpandableListAdapter());
    }

    @Override
    protected void onDestroy() {
        mDao = null;
        db.close();
        super.onDestroy();
    }

    private class MyExpandableListAdapter extends BaseExpandableListAdapter{

        /**
         * 获取有多少个分组
         * @return 个数
         */
        @Override
        public int getGroupCount() {
            return mDao.getGroupCount();
        }

        /**
         * 返回某个位置的分组有多少子节点
         * @param groupPosition 位置
         * @return 个数
         */
        @Override
        public int getChildrenCount(int groupPosition) {
            return mDao.getChildrenCountByGroupPosition(groupPosition);
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView == null){
                tv = new TextView(mContext);
            }else {
                tv = (TextView) convertView;
            }
            tv.setPadding(70,0,0,0);
            tv.setTextSize(20);
            tv.setTextColor(Color.RED);
            tv.setText(mDao.getNameByGroupPosition(groupPosition));
            return tv;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            TextView tv;
            if(convertView == null){
                tv = new TextView(mContext);
            }else {
                tv = (TextView) convertView;
            }
            tv.setPadding(30,0,0,0);
            tv.setTextSize(16);
            tv.setTextColor(Color.BLACK);
            tv.setText(mDao.getChildrenNameByPosition(groupPosition,childPosition));
            return tv;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
