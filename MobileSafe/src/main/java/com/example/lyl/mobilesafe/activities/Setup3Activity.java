package com.example.lyl.mobilesafe.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;
import com.example.lyl.mobilesafe.Utils.ToastUtils;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class Setup3Activity extends SetupBaseActivity{

    private EditText et_setup3_phoneNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup3);
        et_setup3_phoneNumber = (EditText) findViewById(R.id.et_setup3_phoneNumber);
        et_setup3_phoneNumber.setText(sp.getString("phoneNumber",""));
    }

    @Override
    public void showNext() {
        String phoneNumber = et_setup3_phoneNumber.getText().toString().trim();
        if(TextUtils.isEmpty(phoneNumber)){
            ToastUtils.show(this,"安全号码不能为空");
            return;
        }
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("phoneNumber", phoneNumber);
        editor.apply();
        IntentUtils.startActivityAndFinish(Setup3Activity.this,Setup4Activity.class);
    }

    @Override
    public void ShowPrevious() {
        IntentUtils.startActivityAndFinish(Setup3Activity.this,Setup2Activity.class);
    }

    public void selectContacts(View view){
        //选择联系人 开启一个新的界面 并返回手机号码
        Intent intent = new Intent(this,SelectContactsActivity.class);
        startActivityForResult(intent,0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null){
            return;
        }
        String phoneNumber = data.getStringExtra("phoneNumber").replace("-","").trim();
        et_setup3_phoneNumber.setText(phoneNumber);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
