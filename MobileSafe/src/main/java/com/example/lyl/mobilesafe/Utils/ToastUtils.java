package com.example.lyl.mobilesafe.Utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class ToastUtils {

    /**
     * 显示土司的工具类，安全的工具类可以在任意线程里面显示 默认时长Toast.LENGTH_SHORT
     * @param activity 当前的activity
     * @param text 文本内容
     */
    public static void show(final Activity activity, final String text){
        if("main".equals(Thread.currentThread().getName())){
            Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
        }else {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity,text,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
