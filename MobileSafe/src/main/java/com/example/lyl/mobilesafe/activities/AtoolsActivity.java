package com.example.lyl.mobilesafe.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.engine.SmsTools;

/**
 * Created by lyl on 2016/7/1.
 *
 */
public class AtoolsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 号码归属地查询
     * @param view view对象
     */
    public void queryAddress(View view){
        IntentUtils.startActivity(this, QueryAddressActivity.class);
    }

    /**
     * 常用号码查询
     * @param view view对象
     */
    public void queryCommonum(View view){
        IntentUtils.startActivity(this, CommoNumActivity.class);
    }

    /**
     * 短信备份
     * @param view view对象
     */
    public void backupSms(View view){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("正在备份中...");
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                boolean result = SmsTools.backupSms(AtoolsActivity.this, new SmsTools.BackupSmsCallback() {
                    @Override
                    public void beforeSmsBackup(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void onSmsBackup(int process) {
                        dialog.setProgress(process);
                    }

                }, "backupSms.xml");
                if(result){
                    ToastUtils.show(AtoolsActivity.this,"备份成功");
                }else {
                    ToastUtils.show(AtoolsActivity.this,"备份失败");
                }
                dialog.dismiss();
            }
        }.start();
    }

    /**
     * 还原备份的短信
     * @param view view对象
     */
    public void restoreSms(View view){
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setMessage("正在还原中...");
        dialog.show();
        new Thread(){
            @Override
            public void run() {
                boolean result = SmsTools.restoreSms(AtoolsActivity.this, new SmsTools.restoreSmsCallback() {
                    @Override
                    public void beforeSmsRestore(int max) {
                        dialog.setMax(max);
                    }

                    @Override
                    public void onSmsRestore(int process) {
                        dialog.setProgress(process);
                    }
                }, "backupSms.xml");
                if(result){
                    ToastUtils.show(AtoolsActivity.this,"还原成功");
                }else {
                    ToastUtils.show(AtoolsActivity.this,"还原失败");
                }
                dialog.dismiss();
            }
        }.start();
    }

    /**
     * 打开程序锁
     * @param view view对象
     */
    public void openAppLockSetting(View view){
        IntentUtils.startActivity(AtoolsActivity.this,AppLockActivity.class);
    }
}
