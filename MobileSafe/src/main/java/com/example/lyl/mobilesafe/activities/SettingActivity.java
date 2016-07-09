package com.example.lyl.mobilesafe.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ServiceStatusUtils;
import com.example.lyl.mobilesafe.service.CallSmsSafeService;
import com.example.lyl.mobilesafe.service.ShowAddressService;
import com.example.lyl.mobilesafe.service.WatchDogService;
import com.example.lyl.mobilesafe.view.SettingChangeView;
import com.example.lyl.mobilesafe.view.SettingCheckView;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class SettingActivity extends AppCompatActivity implements View.OnClickListener{

    private SettingCheckView scv_setting_applockwatchdog;
    private SettingCheckView scv_setting_autoUpdate;
    private SharedPreferences sp;
    private SettingCheckView scv_setting_blacklist;
    private SettingCheckView scv_setting_location;
    private SettingChangeView scv_change_bg;
    private static final String[] items = {"半透明","活力橙","卫士蓝","金属灰","苹果绿"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        scv_setting_autoUpdate = (SettingCheckView) findViewById(R.id.scv_setting_autoUpdate);
        scv_setting_blacklist = (SettingCheckView) findViewById(R.id.scv_setting_blacklist);
        scv_setting_location = (SettingCheckView) findViewById(R.id.scv_setting_location);
        scv_change_bg = (SettingChangeView) findViewById(R.id.scv_change_bg);
        scv_setting_applockwatchdog = (SettingCheckView) findViewById(R.id.scv_setting_applockwatchdog);

        scv_setting_location.setOnClickListener(this);
        scv_setting_blacklist.setOnClickListener(this);
        scv_setting_autoUpdate.setOnClickListener(this);
        scv_setting_applockwatchdog.setOnClickListener(this);
        sp = getSharedPreferences("config",MODE_PRIVATE);
        scv_change_bg.setDesc(items[sp.getInt("whichcolor",0)]);
        boolean update = sp.getBoolean("update",false);
        scv_setting_autoUpdate.setChecked(update);
    }


    @Override
    protected void onStart() {
        scv_setting_location.setChecked(ServiceStatusUtils.isServiceRunning(this,"com.example.lyl.mobilesafe.service.ShowAddressService"));
        scv_setting_blacklist.setChecked(ServiceStatusUtils.isServiceRunning(this,"com.example.lyl.mobilesafe.service.CallSmsSafeService"));
        scv_setting_applockwatchdog.setChecked(ServiceStatusUtils.isServiceRunning(this,"com.example.lyl.mobilesafe.service.WatchDogService"));
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scv_setting_autoUpdate:
                settingAutoUpdate();
                break;
            case R.id.scv_setting_blacklist:
                settingBlackList();
                break;
            case R.id.scv_setting_location:
                settingLocation();
                break;
            case R.id.scv_setting_applockwatchdog:
                settingAppLockWatchDog();
                break;

        }
    }

    /**
     * 设置程序锁看门狗
     */
    public void settingAppLockWatchDog() {
        if(scv_setting_applockwatchdog.isChecked()){
            scv_setting_applockwatchdog.setChecked(false);
            Intent service = new Intent(SettingActivity.this, WatchDogService.class);
            stopService(service);
        }else {
            scv_setting_applockwatchdog.setChecked(true);
            Intent service = new Intent(SettingActivity.this, WatchDogService.class);
            startService(service);
        }
    }

    /**
     * 开始电话号码归属地显示
     */
    public void settingLocation() {
        if(scv_setting_location.isChecked()){
            scv_setting_location.setChecked(false);
            Intent service = new Intent(SettingActivity.this, ShowAddressService.class);
            stopService(service);
        }else {
            scv_setting_location.setChecked(true);
            Intent service = new Intent(SettingActivity.this, ShowAddressService.class);
            startService(service);
        }
    }

    /**
     * 开启黑名单拦截
     */
    public void settingBlackList(){
        if(scv_setting_blacklist.isChecked()){
            scv_setting_blacklist.setChecked(false);
            Intent service = new Intent(SettingActivity.this, CallSmsSafeService.class);
            stopService(service);
        }else {
            scv_setting_blacklist.setChecked(true);
            Intent service = new Intent(SettingActivity.this, CallSmsSafeService.class);
            startService(service);
        }
    }

    /**
     * 开启自动更新
     */
    public void settingAutoUpdate() {
        SharedPreferences.Editor editor = sp.edit();
        if(scv_setting_autoUpdate.isChecked()){
            scv_setting_autoUpdate.setChecked(false);
            editor.putBoolean("update",false);
        }else {
            scv_setting_autoUpdate.setChecked(true);
            editor.putBoolean("update",true);
        }
        editor.apply();
    }

    /**
     * 更改归属地提示框的背景
     * @param view view对象
     */
    public void changeLocationBg(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("归属地提示框风格");
        builder.setSingleChoiceItems(items,sp.getInt("whichcolor",0), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putInt("whichcolor",which);
                editor.apply();
                scv_change_bg.setDesc(items[which]);
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
