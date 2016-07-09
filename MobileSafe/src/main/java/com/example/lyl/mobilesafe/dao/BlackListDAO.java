package com.example.lyl.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.lyl.mobilesafe.db.BlackListDBOpenHelper;
import com.example.lyl.mobilesafe.javabean.BlackNumberInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/6/29.
 * 操作黑名单数据库，提供增删改查的方法
 */
public class BlackListDAO {

    private BlackListDBOpenHelper helper;

    public BlackListDAO(Context context) {
        helper = new BlackListDBOpenHelper(context);
    }

    /**
     * 添加黑名单号码
     * @param phone 黑名单号码
     * @param mode 拦截模式
     * @return true表示成功 false表示失败
     */
    public boolean add(String phone,String mode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone",phone);
        values.put("mode",mode);
        long rowId = db.insert("blacklist", null, values);
        db.close();
        return rowId != -1;
    }

    /**
     * 删除黑名单号码
     * @param phone 黑名单号码
     * @return true表示成功 false表示失败
     */
    public boolean delete(String phone){
        SQLiteDatabase db = helper.getWritableDatabase();
        int rowCount = db.delete("blacklist", "phone = ?", new String[]{phone});
        db.close();
        return rowCount != 0;
    }

    /**
     * 修改黑名单号码
     * @param phone 黑名单号码
     * @param newMode 新的拦截模式
     * @return true表示成功 false表示失败
     */
    public boolean update(String phone,String newMode){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode",newMode);
        int row = db.update("blacklist",values,"phone = ?",new String[]{phone});
        db.close();
        return row != 0;
    }


    /**
     * 查找黑名单号码的拦截模式
     * @param phone 黑名单号码
     * @return 拦截模式 1电话拦截 2短信拦截 3全部拦截 如果返回null代表不是黑名单号码
     */
    public String find(String phone){
        String mode = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacklist", null, "phone = ?", new String[]{phone}, null, null, null);
        while (cursor.moveToNext()){
            mode = cursor.getString(cursor.getColumnIndex("mode"));
            return mode;
        }
        cursor.close();
        db.close();
        return mode;
    }

    /**
     * 返回全部的黑名单号码
     * @return 黑名单list
     */
    public List<BlackNumberInfo> getList(){
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("blacklist", null, null, null, null, null, "id desc" );
        while (cursor.moveToNext()){
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhoneNumber(phoneNumber);
            blackNumberInfo.setMode(mode);
            list.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 返回部分的黑名单号码
     * @param start 开始的位置
     * @param max 最多获取多少条数据
     * @return 黑名单list
     */
    public List<BlackNumberInfo> getPartList(int start,int max){
        List<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
        SQLiteDatabase db = helper.getReadableDatabase();
        String sql = "select phone,mode from blacklist order by id desc limit ? offset ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(max), String.valueOf(start)});
        while (cursor.moveToNext()){
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phone"));
            String mode = cursor.getString(cursor.getColumnIndex("mode"));
            BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
            blackNumberInfo.setPhoneNumber(phoneNumber);
            blackNumberInfo.setMode(mode);
            list.add(blackNumberInfo);
        }
        cursor.close();
        db.close();
        return list;
    }

    /**
     * 获取黑名单号码的总条目
     * @return 总个数
     */
    public int getTotalCount(){
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from blacklist",null);
        cursor.moveToNext();
        int totalCount = cursor.getInt(0);
        cursor.close();
        db.close();
        return totalCount;
    }

}
