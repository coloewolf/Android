package com.example.lyl.mobilesafe.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.engine.AppInfoProvider;
import com.example.lyl.mobilesafe.javabean.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/3.
 *
 */
public class AppManagerActivity extends AppCompatActivity implements AbsListView.OnScrollListener,AdapterView.OnItemClickListener,View.OnClickListener{

    private MyAdapter adapter;
    private InnerUninstallReceiver receiver;
    private AppInfo appinfo;
    private PopupWindow popup; //整个界面的悬浮窗体，整个界面保证只有一个悬浮窗体
    private TextView tv_appmanager_diskspace;
    private TextView tv_appmanager_sdspace;
    private TextView tv_appmanager_status;
    private ListView lv_appmanager_list;
    private LinearLayout ll_appmanager_loading;
    private List<AppInfo> list;
    private List<AppInfo> userAppList;
    private List<AppInfo> sysAppList;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            adapter = new MyAdapter();
            ll_appmanager_loading.setVisibility(View.INVISIBLE);
            lv_appmanager_list.setAdapter(adapter);
            lv_appmanager_list.setOnScrollListener(AppManagerActivity.this);
            lv_appmanager_list.setOnItemClickListener(AppManagerActivity.this);
            tv_appmanager_status.setText("用户应用("+userAppList.size()+")");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appmanager);
        //注册广播接收者 设定监听事件为应用被卸载时
        receiver = new InnerUninstallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(receiver, filter);
        tv_appmanager_status = (TextView) findViewById(R.id.tv_appmanager_status);
        ll_appmanager_loading = (LinearLayout) findViewById(R.id.ll_appmanager_loading);
        lv_appmanager_list = (ListView) findViewById(R.id.lv_appmanager_list);
        tv_appmanager_diskspace = (TextView) findViewById(R.id.tv_appmanager_diskspace);
        tv_appmanager_sdspace = (TextView) findViewById(R.id.tv_appmanager_sdspace);
        tv_appmanager_diskspace.setText("内存可用："+Formatter.formatFileSize(this,Environment.getDataDirectory().getFreeSpace()));
        tv_appmanager_sdspace.setText("sd卡可用："+Formatter.formatFileSize(this,Environment.getExternalStorageDirectory().getFreeSpace()));
        fillData();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        dismissPopoup();
        super.onDestroy();
    }

    private void fillData() {
        ll_appmanager_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                userAppList = new ArrayList<AppInfo>();
                sysAppList = new ArrayList<AppInfo>();
                list = AppInfoProvider.getAppInfo(AppManagerActivity.this);
                for (AppInfo info:list) {
                    if(info.isUserApp()){
                        userAppList.add(info);
                    }else {
                        sysAppList.add(info);
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    /**
     * listView 滚动监听器
     * @param view view对象
     * @param scrollState 滚动状态
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    /**
     * 当listview列表只要滚动就被调用
     * @param view view对象
     * @param firstVisibleItem 第一个可见view对象所在的位置
     * @param visibleItemCount 可见view对象的总数
     * @param totalItemCount 所有view对象的总数
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(userAppList != null && sysAppList != null) {
            if (firstVisibleItem > userAppList.size()) {
                tv_appmanager_status.setText("系统应用(" + sysAppList.size() + ")");
            } else {
                tv_appmanager_status.setText("用户应用(" + userAppList.size() + ")");
            }
            dismissPopoup();
        }
    }

    /**
     * 关闭当前界面的弹出窗体
     */
    public void dismissPopoup() {
        if(popup != null && popup.isShowing()){
            popup.dismiss();
            popup = null;
        }
    }

    /**
     * listview条目被点击的监听事件
     * @param parent 父view
     * @param view 当前view对象
     * @param position 当前位置
     * @param id ?
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //根据位置信息，确定哪个条目被点击
        if(position == 0){
            return;
        }else if(position == (userAppList.size()+1)){
            return;
        }else if(position <= userAppList.size()) {
            //用户程序
            appinfo = userAppList.get(position - 1);
        }else {
            //系统程序
            appinfo = sysAppList.get(position - 2 - userAppList.size());
        }
        dismissPopoup();
        View contentView = View.inflate(this, R.layout.items_popup, null);
        //必须要有背景才能显示动画 设置背景色为透明 ? 测试无需设置背景色? 版本问题?
        //contentView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        popup = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
        int[] location = new int[2];
        view.getLocationInWindow(location);
        popup.showAtLocation(parent, Gravity.START+Gravity.TOP,60,location[1]);
        AlphaAnimation aa = new AlphaAnimation(0.2f,1.0f);
        aa.setDuration(300);
        ScaleAnimation sa = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(300);
        AnimationSet set = new AnimationSet(false);
        set.addAnimation(aa);
        set.addAnimation(sa);
        contentView.startAnimation(set);
        LinearLayout ll_uninstall = (LinearLayout) contentView.findViewById(R.id.ll_uninstall);
        ll_uninstall.setOnClickListener(this);
        LinearLayout ll_start = (LinearLayout) contentView.findViewById(R.id.ll_start);
        ll_start.setOnClickListener(this);
        LinearLayout ll_share = (LinearLayout) contentView.findViewById(R.id.ll_share);
        ll_share.setOnClickListener(this);
        LinearLayout ll_info = (LinearLayout) contentView.findViewById(R.id.ll_info);
        ll_info.setOnClickListener(this);
    }

    /**
     * popup点击事件
     * @param v view对象
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_uninstall:
                uninstallApp();
                break;
            case R.id.ll_start:
                startApp();
                break;
            case R.id.ll_share:
                shareApp();
                break;
            case R.id.ll_info:
                showAppInfo();
                break;
        }
        dismissPopoup();
    }

    /**
     * 显示应用的详细信息  备注：可用
     */
    public void showAppInfo() {
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:"+appinfo.getPackageName()));
        startActivity(intent);
    }


    /**
     * 开启应用 备注：可用
     */
    public void startApp() {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appinfo.getPackageName());
        if(intent != null){
            startActivity(intent);
        }else {
            ToastUtils.show(this,"当前应用无法启动");
        }
    }

    /**
     * 卸载应用  备注：可用
     */
    public void uninstallApp() {
        if(appinfo.isUserApp()) {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.DELETE");
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setData(Uri.parse("package:" + appinfo.getPackageName()));
            startActivity(intent);
        }else {
            ToastUtils.show(this,"系统软件需要有root权限后才能卸载");
        }

    }

    /**
     * 分享应用  备注：可用
     */
    public void shareApp() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "推荐你使用一款软件："+appinfo.getName());
        startActivity(intent);
    }

    /**
     * 自定义listview适配器
     */
    private class MyAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size()+2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            AppInfo appinfo;
            if(position == 0){
                TextView tv = new TextView(AppManagerActivity.this);
               /* tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户程序("+userAppList.size()+")");*/
                return tv;
            }else if(position == (userAppList.size()+1)){
                TextView tv = new TextView(AppManagerActivity.this);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统应用("+sysAppList.size()+")");
                return tv;
            }else if(position <= userAppList.size()) {
                //用户程序
                appinfo = userAppList.get(position - 1);
            }else {
                //系统程序
                appinfo = sysAppList.get(position - 2 - userAppList.size());
            }
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(AppManagerActivity.this, R.layout.items_appinfo, null);
                holder = new ViewHolder();
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_appname = (TextView) view.findViewById(R.id.tv_appname);
                holder.tv_appsize = (TextView) view.findViewById(R.id.tv_appsize);
                holder.tv_location = (TextView) view.findViewById(R.id.tv_location);
                view.setTag(holder);
            }
            holder.tv_appname.setText(appinfo.getName());
            holder.tv_appsize.setText(Formatter.formatFileSize(AppManagerActivity.this, appinfo.getApkSizel()));
            holder.iv_appicon.setImageDrawable(appinfo.getIcon());
            holder.tv_location.setText(appinfo.isInRom() ? "手机内存" : "sd卡");
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
        TextView tv_appname;
        TextView tv_appsize;
        TextView tv_location;
        ImageView iv_appicon;
    }

    /**
     * 监听应用被卸载的广播接收者
     */
    private class InnerUninstallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if(appinfo.isUserApp()){
                userAppList.remove(appinfo);
            }else {
                sysAppList.remove(appinfo);
            }
            adapter.notifyDataSetChanged();
        }
    }
}
