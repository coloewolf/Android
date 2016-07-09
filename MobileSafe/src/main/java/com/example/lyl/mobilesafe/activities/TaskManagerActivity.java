package com.example.lyl.mobilesafe.activities;

import android.app.ActivityManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.SystemInfoUtils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.engine.TaskInfoProvider;
import com.example.lyl.mobilesafe.javabean.ProcessInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/4.
 * 备注：PackageManager 相当于windows系统的软件管理器 获取的是静态的信息
 *      ActivityManager 相当于windows系统的任务管理器 获取的是动态的信息
 */
public class TaskManagerActivity extends AppCompatActivity implements AbsListView.OnScrollListener,AdapterView.OnItemClickListener{

    private TextView tv_taskmanager_processcount;
    private TextView tv_taskmanager_memory;
    private TextView tv_taskmanager_status;
    private int runningProcessCount;
    private Context context;
    private ListView lv_taskmanager_list;
    private LinearLayout ll_taskmanager_loading;
    private List<ProcessInfo> list;
    private List<ProcessInfo> userProcessList; //用户进程集合
    private List<ProcessInfo> sysProcessList; //系统进程集合
    private MyAdapter adapter;
    private long availableMem;
    private long totalMem;
    private SharedPreferences sp;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            ll_taskmanager_loading.setVisibility(View.INVISIBLE);
            adapter = new MyAdapter();
            lv_taskmanager_list.setAdapter(adapter);
            tv_taskmanager_status.setText("用户进程("+userProcessList.size()+")");
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanager);
        tv_taskmanager_status = (TextView) findViewById(R.id.tv_taskmanager_status);
        lv_taskmanager_list = (ListView) findViewById(R.id.lv_taskmanager_list);
        ll_taskmanager_loading = (LinearLayout) findViewById(R.id.ll_taskmanager_loading);
        tv_taskmanager_processcount = (TextView) findViewById(R.id.tv_taskmanager_processcount);
        tv_taskmanager_memory = (TextView) findViewById(R.id.tv_taskmanager_memory);
        context = TaskManagerActivity.this;
        sp = getSharedPreferences("config", MODE_PRIVATE);
        runningProcessCount = SystemInfoUtils.getRunningProcessCount(context);
        availableMem = SystemInfoUtils.getAvailableRam(context);
        totalMem = SystemInfoUtils.getTotalRam(context);
        tv_taskmanager_memory.setText("剩余/总内存：" + Formatter.formatFileSize(context,availableMem)
                + "/" + Formatter.formatFileSize(context,totalMem));
        tv_taskmanager_processcount.setText("运行中进程："+runningProcessCount+"个");
        fillData();
        //给listview注册滚动和点击监听事件
        lv_taskmanager_list.setOnScrollListener(this);
        lv_taskmanager_list.setOnItemClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(adapter != null){
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 填充数据
     */
    private void fillData(){
        ll_taskmanager_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                userProcessList = new ArrayList<ProcessInfo>();
                sysProcessList = new ArrayList<ProcessInfo>();
                list = TaskInfoProvider.getRunningProcess(context);
                for (ProcessInfo info : list ) {
                    if(info.isUserTask()){
                        userProcessList.add(info);
                    }else {
                        sysProcessList.add(info);
                    }
                }
                //通知界面更新
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(userProcessList != null && sysProcessList != null) {
            if (firstVisibleItem > userProcessList.size()) {
                tv_taskmanager_status.setText("系统进程(" + sysProcessList.size() + ")");
            } else {
                tv_taskmanager_status.setText("用户进程(" + userProcessList.size() + ")");
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //根据位置信息，确定哪个条目被点击
        ProcessInfo processInfo;
        if(position == 0){
            return;
        }else if(position == (userProcessList.size()+1)){
            return;
        }else if(position <= userProcessList.size()) {
            //用户进程
            processInfo = userProcessList.get(position - 1);
        }else {
            //系统进程
            processInfo = sysProcessList.get(position - 2 - userProcessList.size());
        }
        if(getPackageName().equals(processInfo.getPackageName())){
            return;
        }
        if(processInfo.isChecked()){
            processInfo.setChecked(false);
        }else {
            processInfo.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * listView 自定义适配器
     */
    private class MyAdapter extends BaseAdapter{

        /**
         * 控制listview里显示多少个条目
         * @return 条目个数
         */
        @Override
        public int getCount() {
            if(sp.getBoolean("isshowsysprocess",true)){
                return userProcessList.size() + sysProcessList.size() + 2;
            }else {
                return userProcessList.size() + 1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            ProcessInfo processInfo;
            if(position == 0){
                TextView tv = new TextView(context);
                return tv;
            }else if(position == (userProcessList.size() + 1)){
                TextView tv = new TextView(context);
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程("+sysProcessList.size()+")");
                return tv;
            }else if(position <= userProcessList.size()){
                //用户进程
                processInfo = userProcessList.get(position - 1);
            } else {
                //系统进程
                processInfo = sysProcessList.get(position - 1 - userProcessList.size() - 1);
            }
            if(convertView != null && convertView instanceof RelativeLayout){
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }else {
                holder = new ViewHolder();
                view = View.inflate(context,R.layout.items_taskinfo,null);
                holder.iv_appicon = (ImageView) view.findViewById(R.id.iv_appicon);
                holder.tv_processname = (TextView) view.findViewById(R.id.tv_processname);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_memsize);
                holder.cb = (CheckBox) view.findViewById(R.id.cb);
                view.setTag(holder);
            }
            holder.iv_appicon.setImageDrawable(processInfo.getIcon());
            holder.tv_processname.setText(processInfo.getAppName());
            holder.tv_memsize.setText("内存占用：" + Formatter.formatFileSize(context, processInfo.getMemSize()));
            holder.cb.setChecked(processInfo.isChecked());
            if(getPackageName().equals(processInfo.getPackageName())){
                holder.cb.setVisibility(View.INVISIBLE);
            }else {
                holder.cb.setVisibility(View.VISIBLE);
            }
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

        private TextView tv_processname;
        private TextView tv_memsize;
        private ImageView iv_appicon;
        private CheckBox cb;
    }

    /**
     * 全选
     * @param view view对象
     */
    public void selectAll(View view) {
        for (ProcessInfo info : userProcessList) {
            if(getPackageName().equals(info.getPackageName())){
                continue;
            }
            info.setChecked(true);
        }
        for (ProcessInfo info : sysProcessList) {
            info.setChecked(true);
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     * @param view view对象
     */
    public void selectOpposite(View view) {
        for (ProcessInfo info : userProcessList) {
            if(getPackageName().equals(info.getPackageName())){
                continue;
            }
            if(info.isChecked()){
                info.setChecked(false);
            }else {
                info.setChecked(true);
            }
        }
        for (ProcessInfo info : sysProcessList) {
            if(info.isChecked()){
                info.setChecked(false);
            }else {
                info.setChecked(true);
            }
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * 一键清理
     * @param view view对象
     */
    public void killSelect(View view) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        //记录被杀掉的进程信息
        List<ProcessInfo> tempList = new ArrayList<ProcessInfo>();
        for (ProcessInfo info : userProcessList) {
            if(info.isChecked()) {
                am.killBackgroundProcesses(info.getPackageName());
                savedMem += info.getMemSize();
                count++;
                tempList.add(info);
            }
        }
        for (ProcessInfo info : sysProcessList) {
            if(info.isChecked()) {
                am.killBackgroundProcesses(info.getPackageName());
                savedMem += info.getMemSize();
                count++;
                tempList.add(info);
            }
        }
        for(ProcessInfo info : tempList){
            if(info.isUserTask()){
                userProcessList.remove(info);
            }else {
                sysProcessList.remove(info);
            }
        }
        ToastUtils.show(TaskManagerActivity.this,"清理了"+count+"个进程。释放了"+
                Formatter.formatFileSize(context,savedMem)+"的空间");

        runningProcessCount -= count;
        availableMem += savedMem;
        tv_taskmanager_memory.setText("剩余/总内存：" + Formatter.formatFileSize(context,availableMem)
                + "/" + Formatter.formatFileSize(context,totalMem));
        tv_taskmanager_processcount.setText("运行中进程："+runningProcessCount+"个");
        adapter.notifyDataSetChanged();
    }

    /**
     * 打开进程管理的设置界面
     * @param view view对象
     */
    public void openSettings(View view){
        IntentUtils.startActivity(TaskManagerActivity.this,TaskManagerSettingsActivity.class);
    }



}
