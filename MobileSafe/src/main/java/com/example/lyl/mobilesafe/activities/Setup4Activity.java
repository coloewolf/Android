package com.example.lyl.mobilesafe.activities;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Receiver.MyAdminReceiver;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.view.SettingCheckView;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class Setup4Activity extends SetupBaseActivity implements View.OnClickListener{

    private SettingCheckView scv_setup4_status;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup4);
        scv_setup4_status = (SettingCheckView) findViewById(R.id.scv_setup4_status);
        scv_setup4_status.setChecked(sp.getBoolean("protecting",false));
        scv_setup4_status.setOnClickListener(this);
    }

    /**
     * 设置完成
     */
    @Override
    public void showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("finishsetup",true);
        editor.apply();
        IntentUtils.startActivityAndFinish(Setup4Activity.this,LostFindActivity.class);
    }

    @Override
    public void ShowPrevious() {
        IntentUtils.startActivityAndFinish(Setup4Activity.this,Setup3Activity.class);
    }


    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sp.edit();
        if(scv_setup4_status.isChecked()){
            scv_setup4_status.setChecked(false);
            editor.putBoolean("protecting", false);
        }else {
            scv_setup4_status.setChecked(true);
            editor.putBoolean("protecting", true);
        }
        editor.apply();
    }

    /**
     * 点击激活设备的超级管理员
     * @param view view对象
     */
    public void activeAdmin(View view){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        ComponentName who = new ComponentName(this, MyAdminReceiver.class);
        //把要激活的组件告诉系统
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, who);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"开启后可以实现远程锁屏和销毁数据");
        startActivity(intent);
    }
}
