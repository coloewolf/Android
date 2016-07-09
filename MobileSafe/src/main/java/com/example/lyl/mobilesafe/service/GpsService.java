package com.example.lyl.mobilesafe.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsManager;

import com.example.lyl.mobilesafe.Utils.ModifyOffsetUtils;
import com.example.lyl.mobilesafe.javabean.PointDouble;

/**
 * Created by lyl on 2016/6/29.
 *
 */
public class GpsService extends Service implements LocationListener {

    private LocationManager lm;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        String provider = lm.getBestProvider(getCriteria(), true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 确认有无权限 无权限返回
            return;
        }
        lm.requestLocationUpdates(provider, 0, 0, this);
    }

    public Criteria getCriteria() {
        Criteria criteria = new Criteria();
        //指定精度
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //指定耗电量
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        return criteria;
    }


    /**
     * 得到位置信息
     * @param location 位置信息
     */
    @Override
    public void onLocationChanged(Location location) {
        try {
            double x = location.getLongitude();
            double y = location.getLatitude();
            ModifyOffsetUtils mou = ModifyOffsetUtils.getInstance(getApplicationContext().getAssets().open("axisoffset.dat"));
            PointDouble pd = mou.s2c(new PointDouble(x,y));
            SharedPreferences sp = getApplicationContext().getSharedPreferences("config", MODE_PRIVATE);
            String phoneNumber = sp.getString("phoneNumber", "");
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, pd.toString(), null, null);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // 确认有无权限 无权限返回
                return;
            }
            //获取位置信息结束关闭更新 和 停止服务
            lm.removeUpdates(this);
            stopSelf();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * provider状态变化时调用
     * @param provider 当前的provider
     * @param status 状态
     * @param extras 数据
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * 当provider可用时调用
     * @param provider 当前的provider
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * 当provider不可用时调用
     * @param provider 当前的provider
     */
    @Override
    public void onProviderDisabled(String provider) {

    }
}
