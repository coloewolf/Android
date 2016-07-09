package com.example.lyl.mobilesafe.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lyl on 2016/7/4.
 *
 */
public class MyWidget extends AppWidgetProvider{

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //开启服务
    }

    @Override
    public void onEnabled(Context context) {

    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

    }

    @Override
    public void onDisabled(Context context) {
        //关闭服务
    }

}
