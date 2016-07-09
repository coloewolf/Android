package com.example.lyl.mobilesafe.javabean;

import android.graphics.drawable.Drawable;

/**
 * Created by lyl on 2016/7/7.
 *
 */
public class CacheInfo {

    private String appPackageName;
    private String appName;
    private Drawable appIcon;
    private long cacheSize;

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public long getCacheSize() {
        return cacheSize;
    }

    public void setCacheSize(long cacheSize) {
        this.cacheSize = cacheSize;
    }
}
