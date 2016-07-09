package com.example.lyl.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.activities.EnterPasswordActivty;
import com.example.lyl.mobilesafe.dao.AppLockDao;

import java.util.List;

/**
 * Created by lyl on 2016/7/6.
 *
 */
public class WatchDogService extends Service{

    private boolean flag;
    private ActivityManager am;
    private AppLockDao dao;
    private InnerWatchDogReceiver receiver;
    private String tempStopAppPackageName; //临时要停止保护的应用的包名
    private List<String> packageList;
    private String name = null;
    private List<ActivityManager.RunningTaskInfo> infoList = null;
    private Intent intent;
    private AppLockDBObserver observer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new InnerWatchDogReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.example.lyl.mobilesafe.STOP");
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(receiver, filter);

        Uri uri = Uri.parse("content://com.example.lyl.mobilesafe.dbchange");
        observer = new AppLockDBObserver(new Handler());
        getContentResolver().registerContentObserver(uri,true,observer);

        intent = new Intent(WatchDogService.this, EnterPasswordActivty.class);
        //服务里没有任务栈信息 如果要在服务里开启Activity 必须添加任务栈flag
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        dao = new AppLockDao(this);
        packageList = dao.findAll();

        am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1000);
        startWatchDog();
    }

    public void startWatchDog() {
        if(flag == true){
            return;
        }
        flag = true;
        new Thread(){
            @Override
            public void run() {
                while (flag){
                    infoList = am.getRunningTasks(1);
                    name = infoList.get(0).topActivity.getPackageName();
                    if(packageList.contains(name)){
                        if(name.equals(tempStopAppPackageName)) {
                            //应用临时停止保护
                        } else {
                            intent.putExtra("apppackagename", name);
                            startActivity(intent);
                        }
                    }else {
                        //应用不需要保护
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(observer);
        observer = null;
        unregisterReceiver(receiver);
        receiver = null;
        flag = false;
    }

    private class InnerWatchDogReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if("com.example.lyl.mobilesafe.STOP".equals(intent.getAction())){
                tempStopAppPackageName = intent.getStringExtra("apppackagename");
            }else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())){
                tempStopAppPackageName = null;
                flag = false;
            }else if(Intent.ACTION_SCREEN_ON.equals(intent.getAction())){
                startWatchDog();
            }
        }
    }

    private class AppLockDBObserver extends ContentObserver{

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public AppLockDBObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            packageList = dao.findAll();
        }
    }
}
