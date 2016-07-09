package com.example.lyl.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;

import org.w3c.dom.Text;

/**
 * Created by lyl on 2016/7/1.
 *
 */
public class SettingChangeView extends RelativeLayout{

    private TextView tv_ui_title;
    private TextView tv_ui_content;

    public SettingChangeView(Context context) {
        super(context);
        initView(context);
    }

    public SettingChangeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        String title = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "title");
        String desc = attrs.getAttributeValue("http://schemas.android.com/apk/res-auto", "desc");
        tv_ui_title.setText(title);
        tv_ui_content.setText(desc);
    }

    public SettingChangeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 初始化控件
     * @param context
     */
    public void  initView(Context context){
        View view = View.inflate(context, R.layout.ui_change_view,this);
        tv_ui_title = (TextView) view.findViewById(R.id.tv_ui_title);
        tv_ui_content = (TextView) view.findViewById(R.id.tv_ui_content);
    }

    /**
     * 设置里面显示的文本
     * @param text
     */
    public void setDesc(String text){
        tv_ui_content.setText(text);
    }


}
