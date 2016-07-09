package com.example.lyl.mobilesafe.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.text.format.Formatter;

import com.example.lyl.mobilesafe.javabean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/3.
 * 业务类，提供系统应用程序的信息
 */
public class AppInfoProvider {

    /**
     * 获取手机里所有应用的信息 用户app目录/data/app/xx 系统app目录/system/app/xx
     * @param context 上下文
     * @return 应用信息的集合
     */
    public static List<AppInfo> getAppInfo(Context context){
        List<AppInfo> AppInfoList = new ArrayList<AppInfo>();
        PackageManager pm = context.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);
        //清单文件的所有内容都封装在PackageInfo里
        for (PackageInfo info : list) {
            AppInfo appInfo = new AppInfo();
            String packageName = info.packageName;
            //获取其它应用的名称和图标必须用load方法
            String appName = info.applicationInfo.loadLabel(pm).toString();
            Drawable icon = info.applicationInfo.loadIcon(pm);
            //获取文件的全路径 利用file得到大小
            String path = info.applicationInfo.sourceDir;
            File file = new File(path);
            long size = file.length();
            appInfo.setName(appName);
            appInfo.setPackageName(packageName);
            appInfo.setApkSizel(size);
            appInfo.setIcon(icon);
            int flag = info.applicationInfo.flags;
            if((flag & ApplicationInfo.FLAG_SYSTEM) == 0){
                //用户程序
                appInfo.setUserApp(true);
            }else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0 ){
                //手机系统内部
                appInfo.setInRom(true);
            }else {
                //sd卡
                appInfo.setInRom(false);
            }
            AppInfoList.add(appInfo);
           /* System.out.println("packName："+packageName);
            System.out.println("appName："+appName);
            System.out.println("path："+path);
            System.out.println("size："+ Formatter.formatFileSize(context,size));*/
        }
        return AppInfoList;
    }
}
