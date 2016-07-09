package com.example.lyl.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class SettingCheckView extends LinearLayout{

    private CheckBox cb_ui_status;

    public SettingCheckView(Context context) {
        super(context);
        initView(context);
    }

    public SettingCheckView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String text = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto","bigTitle");
        TextView tv_ui_setting = (TextView) findViewById(R.id.tv_ui_setting);
        tv_ui_setting.setText(text);
    }

    public SettingCheckView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public void initView(Context context) {
        this.setOrientation(VERTICAL);
        this.addView(View.inflate(context, R.layout.ui_setting_view,null));
        cb_ui_status = (CheckBox) findViewById(R.id.cb_ui_status);
    }

    /**
     * 判断组合控件是否被选中
     * @return ture 代表是 false 代表否
     */
    public boolean isChecked(){
        return cb_ui_status.isChecked();
    }

    /**
     * 设置组合控件的选中状态
     */
    public void setChecked(boolean checked){
        cb_ui_status.setChecked(checked);
    }
}
