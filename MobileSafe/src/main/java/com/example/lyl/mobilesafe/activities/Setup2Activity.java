package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.view.SettingCheckView;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class Setup2Activity extends SetupBaseActivity implements View.OnClickListener{

    private SettingCheckView scv_setup2_bind;
    private TelephonyManager mTelephonyManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup2);
        mTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        scv_setup2_bind = (SettingCheckView) findViewById(R.id.scv_setup2_bind);
        scv_setup2_bind.setOnClickListener(Setup2Activity.this);
        String sim = sp.getString("sim",null);
        if(TextUtils.isEmpty(sim)){
            scv_setup2_bind.setChecked(false);
        }else {
            scv_setup2_bind.setChecked(true);
        }
    }

    @Override
    public void showNext() {
        if(scv_setup2_bind.isChecked()) {
            IntentUtils.startActivityAndFinish(Setup2Activity.this, Setup3Activity.class);
        }else {
            ToastUtils.show(Setup2Activity.this,"要开启手机防盗，必须绑定SIM卡号");
        }

    }

    @Override
    public void ShowPrevious() {
        IntentUtils.startActivityAndFinish(Setup2Activity.this,Setup1Activity.class);
    }


    @Override
    public void onClick(View v) {
        if(scv_setup2_bind.isChecked()){
            //取消绑定，把保存的卡号设置为空
            scv_setup2_bind.setChecked(false);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sim",null);
            editor.apply();
        }else {
            //取得卡号 并保存
            scv_setup2_bind.setChecked(true);
            String simSerialNumberm = mTelephonyManager.getSimSerialNumber();
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("sim",simSerialNumberm);
            editor.apply();
        }
    }
}
