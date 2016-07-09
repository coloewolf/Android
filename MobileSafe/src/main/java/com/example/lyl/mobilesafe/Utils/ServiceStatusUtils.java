package com.example.lyl.mobilesafe.Utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by lyl on 2016/6/30.
 * 服务运行状态的工具类
 */
public class ServiceStatusUtils {

    /**
     * 判断服务是否在运行
     * @param context 上下文
     * @param name 服务的全路径类名
     * @return true代表正在运行 false代表服务以停止
     */
    public static boolean isServiceRunning(Context context,String name){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> list = am.getRunningServices(1000);
        for(ActivityManager.RunningServiceInfo info : list){
            String className = info.service.getClassName();
            if(name.equals(className)){
                return true;
            }
        }
        return false;
    }
}
