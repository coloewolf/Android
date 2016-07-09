package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.dao.AppLockDao;
import com.example.lyl.mobilesafe.engine.AppInfoProvider;
import com.example.lyl.mobilesafe.javabean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/5.
 *
 */
public class AppLockActivity extends AppCompatActivity implements View.OnClickListener,AbsListView.OnScrollListener{

    private Context context;
    private TextView tv_applock_unlockappcount;
    private TextView tv_applock_lock;
    private TextView tv_applock_unlock;
    private TextView tv_applock_status;
    private TextView tv_applock_lockstatus;
    private ListView lv_applock_unlocklist;
    private ListView lv_applock_locklist;
    private LinearLayout ll_applock_lock;
    private LinearLayout ll_applock_unlock;
    private LinearLayout ll_applock_loading;
    private List<AppInfo> list; //所有应用的集合
    private List<AppInfo> userAppList; //用户应用的集合
    private List<AppInfo> sysAppList; //系统应用的集合
    private List<AppInfo> lockAppList; //加锁应用的集合
    private List<AppInfo> lockUserAppList; //用户加锁应用的集合
    private List<AppInfo> lockSysAppList; //系统加锁应用的集合
    private AppLockDao dao;
    private MyAdapter lockAdapter;
    private MyAdapter unlockAdapter;

    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            lockAdapter = new MyAdapter(true);
            unlockAdapter = new MyAdapter(false);
            //数据初始化完毕更新ui
            ll_applock_loading.setVisibility(View.GONE);
            lv_applock_unlocklist.setAdapter(unlockAdapter);
            lv_applock_unlocklist.setOnScrollListener(AppLockActivity.this);
            tv_applock_unlockappcount.setText("未加锁的应用("+(userAppList.size()+sysAppList.size())+")");


            lv_applock_locklist.setAdapter(lockAdapter);

        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applock);
        context = AppLockActivity.this;
        dao = new AppLockDao(context);

        tv_applock_lock = (TextView) findViewById(R.id.tv_applock_lock);
        tv_applock_unlock = (TextView) findViewById(R.id.tv_applock_unlock);
        tv_applock_status = (TextView) findViewById(R.id.tv_applock_status);
        tv_applock_lockstatus = (TextView) findViewById(R.id.tv_applock_lockstatus);
        ll_applock_lock = (LinearLayout) findViewById(R.id.ll_applock_lock);
        ll_applock_unlock = (LinearLayout) findViewById(R.id.ll_applock_unlock);
        ll_applock_loading = (LinearLayout) findViewById(R.id.ll_applock_loading);
        lv_applock_unlocklist = (ListView) findViewById(R.id.lv_applock_unlocklist);
        tv_applock_unlockappcount = (TextView) findViewById(R.id.tv_applock_unlockappcount);
        lv_applock_locklist = (ListView) findViewById(R.id.lv_applock_locklist);

        fillDate();

        tv_applock_lock.setOnClickListener(this);
        tv_applock_unlock.setOnClickListener(this);
        lv_applock_locklist.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(lockUserAppList != null && lockSysAppList != null){
                    if (firstVisibleItem > lockUserAppList.size()){
                        tv_applock_lockstatus.setText("系统应用("+lockSysAppList.size()+")");
                    }else {
                        tv_applock_lockstatus.setText("用户应用("+lockUserAppList.size()+")");
                    }
                }
            }
        });

    }

    /**
     * 初始化数据
     */
    private void fillDate() {
        ll_applock_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                list = AppInfoProvider.getAppInfo(context);
                userAppList = new ArrayList<AppInfo>();
                sysAppList = new ArrayList<AppInfo>();
                lockAppList = new ArrayList<AppInfo>();
                for(AppInfo info : list){
                    if(dao.find(info.getPackageName())){
                        lockAppList.add(info);
                        continue;
                    }
                    if(info.isUserApp()){
                        userAppList.add(info);
                    }else {
                        sysAppList.add(info);
                    }
                }
                lockUserAppList = new ArrayList<AppInfo>();
                lockSysAppList = new ArrayList<AppInfo>();
                if (lockAppList != null) {
                    for (AppInfo info : lockAppList) {
                        if (info.isUserApp()) {
                            lockUserAppList.add(info);
                        } else {
                            lockSysAppList.add(info);
                        }
                    }
                }
                hanlder.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_applock_lock:
                tv_applock_lockstatus.setVisibility(View.VISIBLE);
                tv_applock_status.setVisibility(View.INVISIBLE);
                ll_applock_lock.setVisibility(View.VISIBLE);
                ll_applock_unlock.setVisibility(View.INVISIBLE);
                tv_applock_unlock.setBackgroundResource(R.drawable.tab_left_default);
                tv_applock_lock.setBackgroundResource(R.drawable.tab_right_pressed);
                tv_applock_lockstatus.setText("用户应用("+lockUserAppList.size()+")");
                tv_applock_unlockappcount.setText("已加锁的应用("+(lockUserAppList.size()+lockSysAppList.size())+")");
                break;
            case R.id.tv_applock_unlock:
                tv_applock_status.setVisibility(View.VISIBLE);
                tv_applock_lockstatus.setVisibility(View.INVISIBLE);
                ll_applock_unlock.setVisibility(View.VISIBLE);
                ll_applock_lock.setVisibility(View.INVISIBLE);
                tv_applock_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
                tv_applock_lock.setBackgroundResource(R.drawable.tab_right_default);
                tv_applock_unlockappcount.setText("未加锁的应用("+(userAppList.size()+sysAppList.size())+")");
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(userAppList != null && sysAppList != null){
            if (firstVisibleItem > userAppList.size()){
                tv_applock_status.setText("系统应用("+sysAppList.size()+")");
            }else {
                tv_applock_status.setText("用户应用("+userAppList.size()+")");
            }
        }
    }


    /**
     * 自定义的listview适配器
     */
    private class MyAdapter extends BaseAdapter{

        private boolean isLock; //true表示以加锁 false表示未加锁

        public MyAdapter(boolean isLock){
            this.isLock = isLock;
        }


        @Override
        public int getCount() {
            int count = 0;
            if(isLock){
                if(lockSysAppList.size() == 0){
                    count = lockUserAppList.size() + 1;
                }else
                    count = lockUserAppList.size() + lockSysAppList.size() + 2;
            }else {
                count = userAppList.size() + sysAppList.size() + 2;
            }
            return count;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final View view;
            ViewHolder holder;
            final AppInfo appInfo;
            if( position == 0){
                TextView tv = new TextView(context);
                return tv;
            } else if (position == ((isLock ? lockUserAppList.size() : userAppList.size())+1)) {
                TextView tv = new TextView(context);
                tv.setPadding(20, 0, 0, 0);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统应用(" + (isLock ? lockSysAppList.size() : sysAppList.size()) + ")");
                return tv;
            } else if (position <= (isLock ? lockUserAppList.size() : userAppList.size())) {
                appInfo = isLock ? lockUserAppList.get(position - 1) : userAppList.get(position - 1);
            } else {
                appInfo = isLock ? lockSysAppList.get((position - 2 - lockUserAppList.size()))
                        : sysAppList.get((position - 2 - userAppList.size()));
            }
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                holder = new ViewHolder();
                view = isLock ? View.inflate(context, R.layout.items_applock_lock, null)
                        : View.inflate(context, R.layout.items_applock_unlock, null);
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                holder.iv_lock = isLock ? (ImageView) view.findViewById(R.id.iv_unlock)
                        : (ImageView) view.findViewById(R.id.iv_lock);
                view.setTag(holder);
            }
            holder.tv_appname.setText(appInfo.getName());
            holder.iv_appicon.setImageDrawable(appInfo.getIcon());
            holder.iv_lock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isLock){
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1.0f,
                                Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(150);
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dao.delete(appInfo.getPackageName());
                                if(appInfo.isUserApp()){
                                    lockUserAppList.remove(appInfo);
                                    userAppList.add(appInfo);
                                }else {
                                    lockSysAppList.remove(appInfo);
                                    sysAppList.add(appInfo);
                                }
                                lockAdapter.notifyDataSetChanged();
                                unlockAdapter.notifyDataSetChanged();
                                tv_applock_unlockappcount.setText("已加锁的应用("+(lockUserAppList.size()+lockSysAppList.size())+")");
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }else {
                        TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1.0f,
                                Animation.RELATIVE_TO_SELF,0,Animation.RELATIVE_TO_SELF,0);
                        ta.setDuration(150);
                        //开子线程 发消息更新界面
                        view.startAnimation(ta);
                        ta.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                dao.add(appInfo.getPackageName());
                                if (appInfo.isUserApp()) {
                                    userAppList.remove(appInfo);
                                    lockUserAppList.add(appInfo);
                                } else {
                                    sysAppList.remove(appInfo);
                                    lockSysAppList.add(appInfo);
                                }
                                tv_applock_unlockappcount.setText("未加锁的应用("+(userAppList.size()+sysAppList.size())+")");
                                lockAdapter.notifyDataSetChanged();
                                unlockAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });
                    }
                    tv_applock_lockstatus.setText("用户应用("+lockUserAppList.size()+")");
                }
            });
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

    private static class ViewHolder{

        private ImageView iv_appicon;
        private TextView tv_appname;
        private ImageView iv_lock;
    }
}
