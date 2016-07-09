package com.example.lyl.mobilesafe.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Created by lyl on 2016/7/4.
 *
 */
public class AutoKillService extends Service{

    private InnerLockScreenReceiver receiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        receiver = new InnerLockScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(receiver, filter);
    }


    @Override
    public void onDestroy() {
        if(receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

    private class InnerLockScreenReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infoList =  am.getRunningAppProcesses();
            for(ActivityManager.RunningAppProcessInfo info : infoList){
                am.killBackgroundProcesses(info.processName);
            }
        }
    }
}

