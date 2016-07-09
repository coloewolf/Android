package com.example.lyl.mobilesafe.engine;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.javabean.ProcessInfo;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.jaredrummler.android.processes.models.AndroidAppProcess;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by lyl on 2016/7/4.
 * 进程管理器的业务类
 */
public class TaskInfoProvider {



    /**
     * 返回正在运行的所有进程信息的集合
     * @return 进程信息的集合
     */
    public static List<ProcessInfo> getRunningProcess(Context context){
        List<ProcessInfo> list = new ArrayList<ProcessInfo>();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        //List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
        List<AndroidAppProcess> infoList = AndroidProcesses.getRunningAppProcesses();
        for (AndroidAppProcess info:infoList) {
            ProcessInfo processInfo = new ProcessInfo();
            String packageName = info.getPackageName();
            processInfo.setPackageName(packageName);
            int[] pid = {info.pid};
            long memSize = am.getProcessMemoryInfo(pid)[0].getTotalPrivateDirty()*1024;
            processInfo.setMemSize(memSize);
            try {
                PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
                String name = packageInfo.applicationInfo.loadLabel(pm).toString();
                processInfo.setAppName(name);
                Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
                processInfo.setIcon(icon);
                int flags = packageInfo.applicationInfo.flags;
                if((flags & ApplicationInfo.FLAG_SYSTEM) != 0){
                    //系统进程
                    processInfo.setUserTask(false);
                }else {
                    //用户进程
                    processInfo.setUserTask(true);
                }
            } catch (PackageManager.NameNotFoundException e) {
                processInfo.setAppName(packageName);
                processInfo.setIcon(context.getResources().getDrawable(R.drawable.default_icon));
                e.printStackTrace();
            }
            list.add(processInfo);
        }
        return list;
    }
}
