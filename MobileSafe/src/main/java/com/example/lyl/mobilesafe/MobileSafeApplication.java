package com.example.lyl.mobilesafe;

import android.app.Application;
import android.os.Build;
import android.os.Environment;

import com.example.lyl.mobilesafe.Utils.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

/**
 * Created by lyl on 2016/7/8.
 * 代表的就是当前的应用程序
 * 注：一定要在清单文件中配置
 */
public class MobileSafeApplication extends Application{

    private static final String TAG = "MobileSafeApplication";

    /**
     * 最早被调用的方法
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.currentThread().setUncaughtExceptionHandler(new MyExceptionHandler());

    }

    private class MyExceptionHandler implements Thread.UncaughtExceptionHandler{

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            try {
                Logger.i(TAG, "发生了异常，被捕获了！");
                StringBuilder sb = new StringBuilder();
                Field[] fields = Build.class.getDeclaredFields();
                for(Field field : fields){
                    String value = field.get(null).toString();
                    String name = field.getName();
                    sb.append(name);
                    sb.append(":");
                    sb.append(value);
                    sb.append("\n");
                }
                File file = new File(Environment.getExternalStorageDirectory().getPath(),"error.log");
                FileOutputStream fos = new FileOutputStream(file);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                String errorLog = sw.toString();
                sb.append(errorLog);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //关闭自己线程
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}
