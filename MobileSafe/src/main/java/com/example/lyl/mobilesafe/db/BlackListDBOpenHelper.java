package com.example.lyl.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lyl on 2016/6/29.
 *
 */
public class BlackListDBOpenHelper extends SQLiteOpenHelper{

    /**
     * 创建一个数据库blacklist.db
     */
    public BlackListDBOpenHelper(Context context) {
        super(context, "blacklist.db", null, 1);
    }

    /**
     * 当数据库第一次创建的时调用，适合初始化数据库表结构
      * @param db blacklist.db _id主键 phone要拦截的电话号码 mode拦截的模式 1电话拦截 2短信拦截 3全部拦截
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacklist (id integer primary key autoincrement,phone varchar(20),mode varchar(2));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
