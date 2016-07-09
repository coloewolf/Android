package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.ToastUtils;
import com.example.lyl.mobilesafe.dao.AppLockDao;

/**
 * Created by lyl on 2016/7/6.
 *
 */
public class EnterPasswordActivty extends AppCompatActivity implements View.OnClickListener{

    private EditText et_enterpassword_password;
    private Button et_enterpassword_accept;
    private ImageView iv_enterpassword_appicon;
    private TextView tv_enterpassword_appname;
    private PackageManager pm;
    private Context context;
    private AppLockDao dao;
    private String appPackageName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enterpassword);
        context = EnterPasswordActivty.this;

        et_enterpassword_password = (EditText) findViewById(R.id.et_enterpassword_password);
        et_enterpassword_accept = (Button) findViewById(R.id.et_enterpassword_accept);
        iv_enterpassword_appicon = (ImageView) findViewById(R.id.iv_enterpassword_appicon);
        tv_enterpassword_appname = (TextView) findViewById(R.id.tv_enterpassword_appname);

        et_enterpassword_accept.setOnClickListener(this);

        pm = getPackageManager();
        Intent data = getIntent();
        appPackageName = data.getStringExtra("apppackagename");
        try {
            PackageInfo info = pm.getPackageInfo(appPackageName, 0);
            String appname = info.applicationInfo.loadLabel(pm).toString();
            Drawable appicon = info.applicationInfo.loadIcon(pm);
            iv_enterpassword_appicon.setImageDrawable(appicon);
            tv_enterpassword_appname.setText(appname);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * 屏蔽后退键，直接跳回桌面
     */
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        String password = et_enterpassword_password.getText().toString().trim();
        if(TextUtils.isEmpty(password)){
            ToastUtils.show(EnterPasswordActivty.this,"密码不能为空");
            return;
        }
        if("123".equals(password)){
            //activity给服务一个消息，发送一个自定义的广播 只有看门狗听的懂
            Intent inent = new Intent();
            inent.setAction("com.example.lyl.mobilesafe.STOP");
            inent.putExtra("apppackagename", appPackageName);
            sendBroadcast(inent);
            finish();
        }else {
            ToastUtils.show(EnterPasswordActivty.this,"密码错误");
        }
    }
}
