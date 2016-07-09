package com.example.lyl.mobilesafe.Utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.example.lyl.mobilesafe.javabean.ContactsInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class ContactsInfoUtils {

    /**
     * 联系人信息的工具类
     * @param context 上下文
     * @return 返回联系人姓名和号码的集合
     */
    public static List<ContactsInfo> getContactsInfo(Context context){
        List<ContactsInfo> list = new ArrayList<ContactsInfo>();
        //内容提供者的解析器
        ContentResolver resolver = context.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = resolver.query(uri, null, null, null, null);
        while (cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            ContactsInfo contactsInfo = new ContactsInfo();
            contactsInfo.setName(name);
            contactsInfo.setPhoneNumber(phoneNumber);
            list.add(contactsInfo);
        }
        cursor.close();
        return list;

    }

}
