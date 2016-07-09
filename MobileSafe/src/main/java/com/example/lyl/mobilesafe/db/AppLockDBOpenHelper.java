package com.example.lyl.mobilesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by lyl on 2016/7/5.
 *
 */
public class AppLockDBOpenHelper extends SQLiteOpenHelper{

    public AppLockDBOpenHelper(Context context) {
        super(context, "applock.db", null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "create table applockinfo(id integer primary key autoincrement,packagename varchar(100),isuserapp integer(2));";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* String sql1 ="drop table applockinfo;";
        db.execSQL(sql1);
        String sql2 = "create table applockinfo(id integer primary key autoincrement,packagename varchar(100));";
        db.execSQL(sql2);*/
    }
}
