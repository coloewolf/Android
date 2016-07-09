package com.example.lyl.mobilesafe.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class BootCompeteReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        boolean protecting = sp.getBoolean("protecting",false);
        //开启防盗保护
        if(protecting) {
            //取出当前手机里面的sim卡号
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String realsim = telephonyManager.getSimSerialNumber();
            //取出原来绑定的sim卡号
            String bindsim = sp.getString("sim", null);
            //检查两次卡号是否一致
            if (realsim.equals(bindsim)) {
                //sim卡号没变化
            } else {
                //sim卡号变化可能被盗 偷偷的在后台发送报警短信
                SmsManager.getDefault().sendTextMessage(sp.getString("phoneNumber",""),null,"sim changed",null,null);
            }
        }else {
            //没开启防盗保护
        }
    }
}
