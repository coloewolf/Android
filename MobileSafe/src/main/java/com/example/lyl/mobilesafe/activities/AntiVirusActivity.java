package com.example.lyl.mobilesafe.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.lyl.mobilesafe.R;
import com.example.lyl.mobilesafe.Utils.Md5Utils;
import com.example.lyl.mobilesafe.dao.AntiVirusDao;

import java.util.List;

/**
 * Created by lyl on 2016/7/7.
 *
 */
public class AntiVirusActivity extends AppCompatActivity{

    private ImageView iv_antivirus_scan;
    private LinearLayout ll_antivirus_list;
    private ProgressBar pb_antivirus_scan;
    private Context context;
    private PackageManager pm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_antivirus);
        context = AntiVirusActivity.this;
        pm = getPackageManager();

        iv_antivirus_scan = (ImageView) findViewById(R.id.iv_antivirus_scan);
        pb_antivirus_scan = (ProgressBar) findViewById(R.id.pb_antivirus_scan);
        ll_antivirus_list = (LinearLayout) findViewById(R.id.ll_antivirus_list);


        RotateAnimation ra = new RotateAnimation(0, 360,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(4000);
        ra.setRepeatCount(Animation.INFINITE);
        ra.setInterpolator(AnimationUtils.loadInterpolator(context,android.R.anim.linear_interpolator));
        iv_antivirus_scan.startAnimation(ra);

        scanVirus();


        //设置隐藏APP 注：开个子线程
        //pm.setComponentEnabledSetting(getComponentName(),PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
    }



    /**
     * 扫描病毒
     */
    private void scanVirus() {
        new Thread(){
            @Override
            public void run() {
                //遍历手机中每一个应用的信息 查询它门的状态码在病毒数据库中是否存在
                List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
                pb_antivirus_scan.setMax(infos.size());
                int progress = 0;
                for(PackageInfo packageInfo : infos){
                    String apkPath = packageInfo.applicationInfo.sourceDir;
                    final String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    String md5 = Md5Utils.md5encode(apkPath,"salt");
                    final String result = AntiVirusDao.isVirus(context, md5);
                    progress++;
                    pb_antivirus_scan.setProgress(progress);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv = new TextView(context);
                            if(result != null){
                                //发现病毒
                                tv.setText(appName+":发现病毒");
                                tv.setTextColor(Color.RED);
                            }else {
                                //安全
                                tv.setText(appName+":安全");
                                tv.setTextColor(Color.GREEN);
                            }
                            ll_antivirus_list.addView(tv,0);
                        }
                    });
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.start();
    }


}
