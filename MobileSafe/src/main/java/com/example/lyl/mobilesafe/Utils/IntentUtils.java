package com.example.lyl.mobilesafe.Utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class IntentUtils {

    /**
     * 开启一个activity
     * @param activity activity
     * @param clazz 想要跳转界面的字节码
     */
    public static void startActivity(Activity activity, Class<?> clazz){
        Intent intent = new Intent(activity,clazz);
        activity.startActivity(intent);
    }

    /**
     * 延迟开启一个activity
     * @param activity 当前activity
     * @param clazz 想要跳转界面的字节码
     * @param delaytime 延迟时间毫秒
     */
    public static void startActivityForDelay(final Activity activity, final Class<?> clazz, final long delaytime){
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity,clazz);
                activity.startActivity(intent);
            }
        }.start();
    }

    /**
     * 开启一个activity 并结束
     * @param activity 当前activity
     * @param clazz 想要跳转界面的字节码
     */
    public static void startActivityAndFinish(final Activity activity, final Class<?> clazz){
        Intent intent = new Intent(activity,clazz);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 延迟开启一个activity 并结束
     * @param activity 当前activity
     * @param clazz 想要跳转界面的字节码
     * @param delaytime 延迟时间毫秒
     */
    public static void startActivityForDelayAndFinish(final Activity activity, final Class<?> clazz, final long delaytime){
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(delaytime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(activity,clazz);
                activity.startActivity(intent);
                activity.finish();
            }
        }.start();
    }
}
