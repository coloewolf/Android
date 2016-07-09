package com.example.lyl.mobilesafe.engine;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by lyl on 2016/7/2.
 * 短信的业务类
 */
public class SmsTools {

    /**
     *
     */
    public interface BackupSmsCallback{

        /**
         * 短信备份之前调用的方法
         * @param max 短信的总个数
         */
        public void beforeSmsBackup(int max);


        /**
         * 当短信备份过程中掉用的方法
         * @param process 当前备份的进度
         */
        public void onSmsBackup(int process);
    }

    public interface restoreSmsCallback{

        /**
         * 短信还原之前调用的方法
         * @param max 短信的总个数
         */
        public void beforeSmsRestore(int max);

        /**
         * 短信还原过程中掉用的方法
         * @param process
         */
        public void onSmsRestore(int process);


    }


    /**
     * 备份用户的短信
     * @param context 上下文
     * @param callback 短信备份的接口
     * @param fileName 备份的文件名称
     * @return 是否备份成功
     */
    public static boolean backupSms(Context context, BackupSmsCallback callback,  String fileName){
        try {
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");
            File file = new File(context.getFilesDir(), fileName);
            FileOutputStream fos = new FileOutputStream(file);
            XmlSerializer serializer = Xml.newSerializer();
            serializer.setOutput(fos,"utf-8");
            serializer.startDocument("utf-8",true);
            serializer.startTag(null, "info");
            Cursor cursor = resolver.query(uri, new String[]{"address", "date", "body", "type"}, null, null, null);
            int max = cursor.getCount();
            serializer.attribute(null, "totle", String.valueOf(max));
            callback.beforeSmsBackup(max);
            int process = 0;
            while (cursor.moveToNext()){
                serializer.startTag(null, "sms");
                serializer.startTag(null, "address");
                String address = cursor.getString(0);
                serializer.text(address);
                serializer.endTag(null, "address");
                serializer.startTag(null, "date");
                String date = cursor.getString(1);
                serializer.text(date);
                serializer.endTag(null, "date");
                serializer.startTag(null, "body");
                String body = cursor.getString(2);
                serializer.text(body);
                serializer.endTag(null, "body");
                serializer.startTag(null, "type");
                String type = cursor.getString(3);
                serializer.text(type);
                serializer.endTag(null, "type");
                serializer.endTag(null, "sms");
                process ++;
                callback.onSmsBackup(process);
            }
            cursor.close();
            serializer.endTag(null, "info");
            serializer.endDocument();
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean restoreSms(Context context, restoreSmsCallback callback,  String fileName){
        try {
            int max = 0;
            int process = 0;
            ContentValues values = null;
            ContentResolver resolver = context.getContentResolver();
            Uri uri = Uri.parse("content://sms");
            File file = new File(context.getFilesDir(), fileName);
            FileInputStream fis = new FileInputStream(file);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            parser.setInput(fis,"utf-8");
            int tag = parser.getEventType();
            while (tag != XmlPullParser.END_DOCUMENT){
                if(tag == XmlPullParser.START_TAG){
                    if("info".equals(parser.getName())){
                        max = Integer.parseInt(parser.getAttributeValue(null,"totle"));
                        callback.beforeSmsRestore(max);
                    }else if("sms".equals(parser.getName())){
                        values = new ContentValues();
                    }else if("address".equals(parser.getName())){
                       // values.put("address",parser.nextText());
                    }else if("date".equals(parser.getName())){
                        //values.put("date",parser.nextText());
                    }else if("body".equals(parser.getName())){
                        values.put("body",parser.nextText());
                        Uri backuri = resolver.insert(uri, values);
                        System.out.println(backuri);
                    }else if("type".equals(parser.getName())){
                        //values.put("type",parser.nextText());
                    }
                }else if(tag == XmlPullParser.END_TAG){
                    if("sms".equals(parser.getName())){

                        process ++;
                        callback.onSmsRestore(process);
                    }
                }
                tag = parser.next();
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
