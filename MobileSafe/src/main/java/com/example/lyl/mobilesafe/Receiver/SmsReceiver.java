package com.example.lyl.mobilesafe.Receiver;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.service.GpsService;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class SmsReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);

        ComponentName who = new ComponentName(context, MyAdminReceiver.class);
        Object[] objs = (Object[]) intent.getExtras().get("pdus");
        for (Object obj : objs) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
            String body = smsMessage.getMessageBody();

            //获取位置信息
            if ("#*location*#".equals(body)) {
                abortBroadcast();
                //开启位置信息服务
                Intent service = new Intent(context, GpsService.class);
                context.startService(service);

            //播放报警音乐
            }else if("#*alarm*#".equals(body)){
                abortBroadcast();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(1.0f,1.0f);
                mediaPlayer.start();

            //清除手机数据
            }else if("#*wipedata*#".equals(body)){
                abortBroadcast();
                if(dpm.isAdminActive(who)) {
                    dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
                }

            //锁屏
            }else if("#*lockscreen".equals(body)){
                abortBroadcast();
                if(dpm.isAdminActive(who)) {
                    dpm.resetPassword("123456", 0);
                    dpm.lockNow();
                }
            }
        }

    }
}
