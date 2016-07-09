package com.example.lyl.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.lyl.mobilesafe.dao.BlackListDAO;

import java.lang.reflect.Method;
import java.net.URL;

/**
 * Created by lyl on 2016/6/30.
 *
 */
public class CallSmsSafeService extends Service{

    private InnerSmsReceiver receiver;
    private BlackListDAO dao;
    //电话管理服务
    private TelephonyManager tm;
    private MyPhoneStateListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dao = new BlackListDAO(this);
        receiver = new InnerSmsReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephany.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(receiver,filter );
        //实例电话管理 并注册监听器
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    private class InnerSmsReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for(Object obj : objs){
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String address = smsMessage.getOriginatingAddress();
                String result = dao.find(address);
                if("2".equals(result) || "3".equals(result)){
                    System.out.println("拦截短信");
                    abortBroadcast();
                }
                String body = smsMessage.getMessageBody();
                if(body.contains("fapiao")){
                    System.out.println("发票短信，拦截...");
                    abortBroadcast();
                }
            }
        }
    }

    private class MyPhoneStateListener extends PhoneStateListener{

        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_IDLE: //待机

                    break;
                case TelephonyManager.CALL_STATE_RINGING: //响铃
                    String result = dao.find(incomingNumber);
                    if("1".equals(result) || "3".equals(result)){
                        System.out.println("黑名单号码，挂断电话");
                        endCall(); //挂断电话呼叫 记录可能还没生成出来
                        deleteCallLog(incomingNumber);
                        //监视呼叫记录的数据库 生成记录时 删除
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            /**
                             * 当观察的数据库 数据发生变化时调用的方法
                             * @param selfChange selfChange
                             */
                            @Override
                            public void onChange(boolean selfChange) {
                                deleteCallLog(incomingNumber);
                            }
                        });
                    }
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK: //接通

                    break;
            }
        }

        /**
         * 删除通话记录
         */
        public void deleteCallLog(String incomingNumber) {
            ContentResolver resolver = getContentResolver();
            Uri uri = Uri.parse("content://call_log/calls");
            resolver.delete(uri,"number = ?",new String[]{incomingNumber});
        }

        /**
         * 利用反射获取binder 利用aidl技术获取ITelephony 实现挂断电话
         */
        public void endCall() {
            try {
                Class clazz = MyPhoneStateListener.class.getClassLoader().loadClass("android.os.ServiceManager");
                Method method = clazz.getDeclaredMethod("getService", String.class);
                IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
                ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
                iTelephony.endCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
