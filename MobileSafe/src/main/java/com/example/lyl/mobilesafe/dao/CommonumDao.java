package com.example.lyl.mobilesafe.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by lyl on 2016/7/2.
 * 常用号码查询dao
 */
public class CommonumDao {

    private SQLiteDatabase db;

    public CommonumDao(SQLiteDatabase db){
        this.db = db;
    }
    /**
     * 返回分组个数
     * @return 个数
     */
    public int getGroupCount(){
        String sql = "select count(*) from classlist";
        Cursor cursor = db.rawQuery(sql,null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }

    /**
     * 根据分组的位置查询子节点的个数
     * @param groupPosition 分组的位置
     * @return 个数
     */
    public int getChildrenCountByGroupPosition(int groupPosition){
        String table = "table" + (groupPosition + 1);
        String sql = "select count(*) from " + table;
        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        return count;
    }


    /**
     * 根据分组的位置查询子节点名称
     * @param groupPosition 分组的位置
     * @return 名称
     */
    public String getNameByGroupPosition(int groupPosition){
        String sql = "select name from classlist where idx = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(groupPosition+1)});
        cursor.moveToNext();
        String name = cursor.getString(0);
        cursor.close();
        return name;
    }

    /**
     * 根据分组的位置和子节点的位置查询子节点的名称
     * @param groupPosition 分组的位置
     * @param childrenPosition 子节点的位置
     * @return 名称
     */
    public String getChildrenNameByPosition(int groupPosition,int childrenPosition){
        String table = "table" + (groupPosition + 1);
        String sql = "select name,number from " + table + " where _id = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(childrenPosition+1)});
        cursor.moveToNext();
        String name = cursor.getString(0);
        String number = cursor.getString(1);
        cursor.close();
        return name + "\n" + number;
    }

}
