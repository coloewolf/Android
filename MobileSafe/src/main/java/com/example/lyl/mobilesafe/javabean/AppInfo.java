package com.example.lyl.mobilesafe.javabean;

import android.graphics.drawable.Drawable;

/**
 * Created by lyl on 2016/7/3.
 *
 */
public class AppInfo {

    private Drawable icon; //应用的图标
    private String name; //应用的名称
    private String packageName; //应用的包名
    private boolean inRom; //是否安装在手机内存里
    private long apkSizel; //应用的大小
    private boolean isUserApp; //是否是用户的应用

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isInRom() {
        return inRom;
    }

    public void setInRom(boolean inRom) {
        this.inRom = inRom;
    }

    public long getApkSizel() {
        return apkSizel;
    }

    public void setApkSizel(long apkSizel) {
        this.apkSizel = apkSizel;
    }

    public boolean isUserApp() {
        return isUserApp;
    }

    public void setUserApp(boolean userApp) {
        isUserApp = userApp;
    }
}
