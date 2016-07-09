package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.dao.PhoneAddressDao;

/**
 * Created by lyl on 2016/7/1.
 *
 */
public class QueryAddressActivity extends AppCompatActivity{

    private EditText et_number;
    private TextView tv_result;
    private Vibrator vibrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queryaddress);
        et_number = (EditText) findViewById(R.id.et_number);
        tv_result = (TextView) findViewById(R.id.tv_result);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

    }

    public void query(View view){
        String number = et_number.getText().toString().trim();
        if(TextUtils.isEmpty(number)){
            ToastUtils.show(this,"号码不能为空");
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.trans_shake);
            et_number.startAnimation(shake);
            vibrator.vibrate(500);
            return;
        }
        try {
            String location = PhoneAddressDao.getAddress(this, number);
            tv_result.setText(location);
        } catch (Exception e) {
            ToastUtils.show(this,"请输入正确的手机号码");
            e.printStackTrace();
        }
    }
}
