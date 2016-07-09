package com.example.lyl.mobilesafe.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.dao.PhoneAddressDao;

/**
 * Created by lyl on 2016/7/1.
 *
 */
public class ShowAddressService extends Service implements View.OnTouchListener{

    private SharedPreferences sp;
    private TelephonyManager tm;
    private MyPhoneStateListener listener;
    private InnerOutCallReceiver receiver;
    private WindowManager wm;
    private View view;
    private int startX;
    private int startY;
    private WindowManager.LayoutParams params;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new MyPhoneStateListener();
        //设置手机来电状态监听器
        tm.listen(listener,PhoneStateListener.LISTEN_CALL_STATE);
        //广播接收者 监听电话呼出
        receiver = new InnerOutCallReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(receiver, filter);

    }

    @Override
    public void onDestroy() {
        tm.listen(listener,PhoneStateListener.LISTEN_NONE);
        listener = null;
        unregisterReceiver(receiver);
        receiver = null;
    }

    /**
     * 自定义的土司
     * @param location 地址
     */
    public void showMyToast(String location) {
        wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        view = View.inflate(this, R.layout.toast_custom, null);
        //设置自定义的背景
        view.setBackgroundResource(getBgResId());
        //设置触摸监听器
        view.setOnTouchListener(this);
        TextView tv_text = (TextView) view.findViewById(R.id.tv_text);
        tv_text.setText(location);
        initParams();
        wm.addView(view, params);
    }


    /**
     * 获取背景图片的id
     * @return 图片id
     */
    public int getBgResId(){
        int which = getSharedPreferences("config",MODE_PRIVATE).getInt("whichcolor",0);
        int[] bgResId = {R.drawable.call_locate_white,R.drawable.call_locate_orange,
                R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
        return bgResId[which];
    }

    /**
     * 初始化params
     */
    public void initParams(){
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        //params.type = WindowManager.LayoutParams.TYPE_TOAST;
        //需要SYSTEM_ALERT_WINDOW权限
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        //初始化坐标到屏幕左上角 为 0,0 与触摸事件保持一致
        params.gravity = Gravity.START + Gravity.TOP;
        //初始化坐标
        params.x = sp.getInt("lastX", 0);
        params.y = sp.getInt("lastY", 0);
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
             // | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE; 不可触摸
    }

    /**
     * 自定义土司的触摸监听器
     * @param v view对象
     * @param event 事件
     * @return true表示动作执行完毕 falas表示还未执行完毕
     */
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN: //手指按下
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE://手指移动
                float endX = event.getRawX();
                float endY = event.getRawY();
                int dX = (int) (endX - startX);
                int dY = (int) (endY - startY);
                params.x += dX;
                params.y += dY;
                //判断坐标是否超出屏幕
                if(params.x < 0) params.x = 0;
                if(params.y < 0) params.y = 0;
                if(params.x > wm.getDefaultDisplay().getWidth() - view.getWidth()) {
                    params.x = wm.getDefaultDisplay().getWidth()- view.getWidth();
                }
                if(params.y > wm.getDefaultDisplay().getHeight() - view.getHeight()) {
                    params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                }
                wm.updateViewLayout(view, params);
                //重新初始化手指位置
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_UP://手指离开
                //保存手指离开屏幕最后的位置
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("lastX", params.x);
                editor.putInt("lastY", params.y);
                editor.apply();
                break;
        }
        return true;
    }


    /**
     * 手机来电状态监听器
     */
    private class MyPhoneStateListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state){
                case TelephonyManager.CALL_STATE_RINGING: //响铃状态
                    String location = PhoneAddressDao.getAddress(getApplicationContext(), incomingNumber.replace("-","").replace(" ",""));
                    //Toast.makeText(getApplicationContext(),location,Toast.LENGTH_SHORT).show();
                    //显示自定义土司
                    System.out.println("当前呼入号码为:"+incomingNumber);
                    showMyToast(location);
                    break;
                case TelephonyManager.CALL_STATE_IDLE: //待机状态
                    //移除土司
                    if(view != null) {
                        wm.removeView(view);
                    }
                    view = null;
                    break;
            }
        }

    }

    /**
     * 广播接收者的内部类 保持和service相同的生命周期
     * 用于监听外拨电话
     */
    private class InnerOutCallReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String outCallNumber = getResultData();
            System.out.println("当前拨打的号码为："+outCallNumber);
            String location = PhoneAddressDao.getAddress(context,outCallNumber.replace("-","").replace(" ",""));
            //Toast.makeText(context,location,Toast.LENGTH_SHORT).show();
            showMyToast(location);
            new Thread(){
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //移除土司
                    if(view != null) {
                        wm.removeView(view);
                    }
                    view = null;
                }
            }.start();
        }
    }


}
