package com.example.lyl.mobilesafe.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.IntentUtils;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class Setup1Activity extends SetupBaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup1);
    }

    @Override
    public void showNext() {
        IntentUtils.startActivityAndFinish(Setup1Activity.this,Setup2Activity.class);
    }

    @Override
    public void ShowPrevious() {
        IntentUtils.startActivityAndFinish(Setup1Activity.this,Setup2Activity.class);
    }

}
