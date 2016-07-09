package com.example.lyl.mobilesafe.Utils;

import android.util.Log;

/**
 * Created by lyl on 2016/7/8.
 *
 */
public class Logger {

    private static int LOGLEVEL = 0; //6不显示日志

    private static final int VERBOSE = 5;
    private static final int DEBUG = 4;
    private static final int INFO = 3;
    private static final int WARN = 2;
    private static final int ERROR = 1;

    public static void v(String tag ,String msg){
        if(VERBOSE > LOGLEVEL){
            Log.v(tag, msg);
        }
    }

    public static void d(String tag ,String msg){
        if(DEBUG > LOGLEVEL){
            Log.d(tag, msg);
        }
    }

    public static void i(String tag ,String msg){
        if(INFO > LOGLEVEL){
            Log.i(tag, msg);
        }
    }

    public static void w(String tag ,String msg){
        if(WARN > LOGLEVEL){
            Log.w(tag, msg);
        }
    }

    public static void e(String tag ,String msg){
        if(ERROR > LOGLEVEL){
            Log.e(tag, msg);
        }
    }


}
