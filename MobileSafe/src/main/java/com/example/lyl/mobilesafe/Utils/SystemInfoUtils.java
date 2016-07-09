package com.example.lyl.mobilesafe.Utils;

import android.app.ActivityManager;
import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

/**
 * Created by lyl on 2016/7/4.
 * 获取手机操作系统的一些信息
 */
public class SystemInfoUtils {

    /**
     * 获取手机里正在运行的进程数量
     * @param context 上下文 要获取手机的状态信息必须要通过context获得ActivityManager
     * @return 进程数量
     */
    public static int getRunningProcessCount(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的应用进程的集合
        List<ActivityManager.RunningAppProcessInfo> infosList = am.getRunningAppProcesses();
        return infosList.size();
    }

    /**
     * 获取手机的剩余可用内存(RAM)的空间
     * @param context 上下文
     * @return 手机的剩余可用内存(RAM)的大小 单位byte
     */
    public static long getAvailableRam(Context context){
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的应用进程的集合
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.availMem;
    }

    /**
     * 获取手机的总内存(RAM)的空间
     * @param context 上下文
     * @return 手机的总内存(RAM)的大小 单位byte 产生异常返回0
     */
    public static long getTotalRam(Context context){
        //高版本用
    /*    ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取正在运行的应用进程的集合
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);
        return outInfo.totalMem;*/
        //低版本可用
        try {
            File file = new File("/proc/meminfo");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            StringBuffer sb = new StringBuffer();
            for (char c:line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }
            //单位是kb 返回byte *1024
            return Long.parseLong(sb.toString()) * 1024;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
