package com.example.lyl.mobilesafe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class LostFindActivity extends AppCompatActivity{

    private ImageView iv_lostfind_status;
    private TextView tv_lostfind_phoneNumber;
    private SharedPreferences sp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lostfind);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        tv_lostfind_phoneNumber = (TextView) findViewById(R.id.tv_lostfind_phoneNumber);
        iv_lostfind_status = (ImageView) findViewById(R.id.iv_lostfind_status);
        tv_lostfind_phoneNumber.setText( sp.getString("phoneNumber", ""));
        boolean protecting = sp.getBoolean("protecting", false);
        if (protecting){
            iv_lostfind_status.setImageResource(R.mipmap.lock);
        }else {
            iv_lostfind_status.setImageResource(R.mipmap.unlock);
        }

    }

    /**
     * 重新进入设置向导
     * @param view view
     */
    public void reEnterSetup(View view){
        IntentUtils.startActivityAndFinish(LostFindActivity.this,Setup1Activity.class);
    }
}
