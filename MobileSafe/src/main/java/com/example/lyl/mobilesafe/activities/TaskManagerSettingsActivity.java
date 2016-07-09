package com.example.lyl.mobilesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ServiceStatusUtils;
import com.example.lyl.mobilesafe.service.AutoKillService;
import com.example.lyl.mobilesafe.view.SettingCheckView;


/**
 * Created by lyl on 2016/7/4.
 * 进程管理的设置界面
 */
public class TaskManagerSettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private SettingCheckView scv_taskmanagersettings_autokill;
    private SettingCheckView scv_taskmanagersettings_isshowsysprocess;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taskmanagersettings);
        scv_taskmanagersettings_isshowsysprocess = (SettingCheckView) findViewById(R.id.scv_taskmanagersettings_isshowsysprocess);
        scv_taskmanagersettings_autokill = (SettingCheckView) findViewById(R.id.scv_taskmanagersettings_autokill);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        scv_taskmanagersettings_isshowsysprocess.setChecked(sp.getBoolean("isshowsysprocess",true));
        scv_taskmanagersettings_isshowsysprocess.setOnClickListener(this);
        scv_taskmanagersettings_autokill.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        if(ServiceStatusUtils.isServiceRunning(getApplicationContext(),"com.example.lyl.mobilesafe.service.AutoKillService")){
            scv_taskmanagersettings_autokill.setChecked(true);
        }else {
            scv_taskmanagersettings_autokill.setChecked(false);
        }
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.scv_taskmanagersettings_isshowsysprocess:
                setShowSysProcess();
                break;
            case R.id.scv_taskmanagersettings_autokill:
                setAutoKill();
                break;
        }
    }

    /**
     * 设置是否锁屏清理
     */
    public void setAutoKill() {
        SharedPreferences.Editor editor = sp.edit();
        if(scv_taskmanagersettings_autokill.isChecked()){
            scv_taskmanagersettings_autokill.setChecked(false);
            editor.putBoolean("isautokill", false);
            Intent serviec = new Intent(TaskManagerSettingsActivity.this, AutoKillService.class);
            stopService(serviec);
        }else {
            scv_taskmanagersettings_autokill.setChecked(true);
            editor.putBoolean("isautokill", true);
            Intent serviec = new Intent(TaskManagerSettingsActivity.this, AutoKillService.class);
            startService(serviec);
        }
        editor.apply();
    }

    /**
     * 设置是否显示系统进程
     */
    public void setShowSysProcess() {
        SharedPreferences.Editor editor = sp.edit();
        if(scv_taskmanagersettings_isshowsysprocess.isChecked()){
            scv_taskmanagersettings_isshowsysprocess.setChecked(false);
            editor.putBoolean("isshowsysprocess", false);
        }else {
            scv_taskmanagersettings_isshowsysprocess.setChecked(true);
            editor.putBoolean("isshowsysprocess", true);
        }
        editor.apply();
    }
}
