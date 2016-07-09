package com.example.lyl.mobilesafe.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lyl on 2016/7/7.
 * 查杀病毒的dao
 */
public class AntiVirusDao {

    /**
     * 判断一个md5信息是否是病毒
     * @param context 上下文
     * @param md5 文件的md5
     * @return 是返回病毒的描述信息 不是返回null
     */
    public static String isVirus(Context context,String md5){
        String desc = null;
        SQLiteDatabase db =
                SQLiteDatabase.openDatabase(context.getFilesDir().getPath()+"/antivirus.db",null,SQLiteDatabase.OPEN_READONLY);
        String sql = "select desc from datable where md5 = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{md5});
        if(cursor.moveToNext()){
            desc = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return desc;
    }

}
