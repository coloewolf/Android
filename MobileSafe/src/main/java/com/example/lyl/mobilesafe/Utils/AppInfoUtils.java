package com.example.lyl.mobilesafe.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class AppInfoUtils {

    /**
     * 获取版本名
     * @param context 上下文
     * @return 成功返回版本名 失败返回0
     */
    public static String getAppVersionName(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),0);
            String versionName = pi.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 获取版本号
     * @param context 上下文
     * @return 成功返回版本号 失败返回0
     */
    public static int getAppVersionCode(Context context){
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(),0);
            int versionCode = pi.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }
}