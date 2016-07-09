package com.example.lyl.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lyl on 2016/7/1.
 * 电话号码归属地查询dao
 */
public class PhoneAddressDao {

    /**
     * 返回电话号码的归属地信息
     * @param phoneNumber 电话号码
     * @return 归属地信息
     */
    public static String getAddress(Context context,String phoneNumber){
        //检查是手机号还是固定电话
        if(!phoneNumber.matches("^1[3578]\\d{9}$")){
            return "请输入正确的手机号码";
        }
        //把location里的内容设置为电话号码
        String location = phoneNumber;
        //把apk里面的数据库文件，拷贝到手机的data/data/包名/files目录
        SQLiteDatabase db = SQLiteDatabase.openDatabase
                (context.getFilesDir().getPath()+"/address.db",null,SQLiteDatabase.OPEN_READONLY);
        String sql = "select location from data2 where id = (select outkey from data1 where id = ?)";
        Cursor cursor = db.rawQuery(sql, new String[]{phoneNumber.substring(0,7)});
        if (cursor.moveToFirst()){
            location = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return "归属地为："+ location;
    }
}
