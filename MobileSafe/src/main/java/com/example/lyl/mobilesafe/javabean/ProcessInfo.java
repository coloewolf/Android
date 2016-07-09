package com.example.lyl.mobilesafe.javabean;

import android.graphics.drawable.Drawable;

/**
 * Created by lyl on 2016/7/4.
 *
 */
public class ProcessInfo {

    private Drawable icon;
    private String appName;
    private long memSize;
    private boolean isUserTask;
    private String packageName;
    private boolean isChecked; //代表当前条目是否被选中

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setUserTask(boolean userTask) {
        isUserTask = userTask;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
