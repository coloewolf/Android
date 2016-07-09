package com.example.lyl.mobilesafe;

import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.LargeTest;

import com.example.lyl.mobilesafe.activities.AtoolsActivity;
import com.example.lyl.mobilesafe.dao.AppLockDao;
import com.example.lyl.mobilesafe.dao.BlackListDAO;
import com.example.lyl.mobilesafe.engine.AppInfoProvider;
import com.example.lyl.mobilesafe.engine.SmsTools;

import org.junit.Test;

import java.util.Random;

/**
 * Created by lyl on 2016/6/29.
 *
 */


public class TestBlackListDao extends ActivityInstrumentationTestCase2<AtoolsActivity>{


    public TestBlackListDao() {
        super(AtoolsActivity.class);
    }


    public void testAdd() throws Exception{
        BlackListDAO dao = new BlackListDAO(getInstrumentation().getTargetContext());
        Random random = new Random();
        for(int i=0 ; i<20 ; i++) {
            boolean result = dao.add("123456"+i, String.valueOf(random.nextInt(3)+1));
        }
    }


    public void test(){
        int i = 1;
        assertEquals(1,i);
    }



    public void testUpdate() throws Exception{
        BlackListDAO dao = new BlackListDAO(getInstrumentation().getTargetContext());
        boolean result = dao.update("123456", "2");
        assertEquals(true,result);
    }


    public void testFind() throws Exception{
        BlackListDAO dao = new BlackListDAO(getInstrumentation().getTargetContext());
        String result = dao.find("123456");
        assertEquals("2",result);
    }


    public void testDelete() throws Exception{
        BlackListDAO dao = new BlackListDAO(getInstrumentation().getTargetContext());
        boolean result = dao.delete("123456");
        assertEquals(true,result);
    }


    public void testBackupSms() throws Exception{
        SmsTools tools = new SmsTools();
        //boolean result = tools.backupSms(getActivity(), "1");

    }


    public void testGetAppInfo() throws Exception{

        AppInfoProvider.getAppInfo(getActivity());
    }

    public void testAppLockDao() throws Exception{
        AppLockDao dao = new AppLockDao(getActivity());

        boolean result = dao.find("com.android.smoketest.tests");
        assertEquals(true,result);
    }
}
