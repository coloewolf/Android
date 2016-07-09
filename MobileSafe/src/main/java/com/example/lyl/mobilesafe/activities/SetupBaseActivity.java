package com.example.lyl.mobilesafe.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.lyl.mobilesafe.R;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public abstract class SetupBaseActivity extends AppCompatActivity{

    public SharedPreferences sp;

    //声明一个手势识别器
    private GestureDetector mGestureDetector;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("config", MODE_PRIVATE);
        mGestureDetector = new GestureDetector(this,new GestureDetector.SimpleOnGestureListener(){
            /**
             * 手指在屏幕上滑动
             * @param e1 手指第一次触摸屏幕
             * @param e2 手指离开屏幕的一瞬间对应的事件
             * @param velocityX 水平方向的速度 px/s
             * @param velocityY 竖直方向的速度 px/s
             * @return ?
             */
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if(Math.abs(e1.getRawY() - e2.getRawY()) > 100){
                    return true;
                }
                if(e1.getRawX() - e2.getRawX() > 150){
                    showNext();
                    overridePendingTransition(R.anim.trans_next_in,R.anim.trans_next_out);
                    return true;
                }
                if(e1.getRawX() - e2.getRawX() < 150){
                    ShowPrevious();
                    overridePendingTransition(R.anim.trans_previous_in,R.anim.trans_previous_out);
                }
                return super.onFling(e1, e2, velocityX, velocityY);
            }
        });

    }

    //使用手势识别器识别用户的动作
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void showNext();

    public abstract void ShowPrevious();

    public void next(View view){
        showNext();
        overridePendingTransition(R.anim.trans_next_in,R.anim.trans_next_out);
    }

    public void previous(View view){
        ShowPrevious();
        overridePendingTransition(R.anim.trans_previous_in,R.anim.trans_previous_out);
    }
}
