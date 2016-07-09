package com.example.lyl.mobilesafe.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.lyl.mobilesafe.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/7/5.
 *
 */
public class AppLockDao {

    private AppLockDBOpenHelper helper;
    private Context context;


    public AppLockDao(Context context){
        helper = new AppLockDBOpenHelper(context);
        this.context = context;
    }

    /**
     * 添加一条应用的包名
     * @param packageName 应用的包名
     * @return true表示添加成功 false表示失败
     */
    public boolean add(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packagename", packageName);
        long result = db.insert("applockinfo", null, values);
        db.close();
        if(result != -1){
            Uri uri = Uri.parse("content://com.example.lyl.mobilesafe.dbchange");
            context.getContentResolver().notifyChange(uri,null);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 删除一条应用的包名
     * @param packageName 应用的包名
     * @return true表示添加成功 false表示失败
     */
    public boolean delete(String packageName){
        SQLiteDatabase db = helper.getWritableDatabase();
        int result = db.delete("applockinfo", "packagename = ?", new String[]{packageName});
        db.close();
        if(result > 0){
            Uri uri = Uri.parse("content://com.example.lyl.mobilesafe.dbchange");
            context.getContentResolver().notifyChange(uri,null);
            return true;
        }else {
            return false;
        }
    }

    /**
     * 查询应用的包名是否需要被锁定
     * @param packageName 应用的包名
     * @return true表示添加成功 false表示失败
     */
    public boolean find(String packageName){
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applockinfo", null, "packagename = ?", new String[]{packageName}, null, null, null);
        result = cursor.moveToNext();
        cursor.close();
        db.close();
        return result;
    }

    /**
     * 查询全部的锁定的应用的包名
     * @return 包名的集合
     */
    public List<String> findAll(){
        List<String> list = new ArrayList<String>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applockinfo", new String[]{"packagename"}, null, null, null, null, null);
        while(cursor.moveToNext()){
            String packageName = cursor.getString(0);
            list.add(packageName);
        }
        cursor.close();
        db.close();
        return list;
    }

}
