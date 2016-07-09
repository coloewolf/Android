package com.example.lyl.mobilesafe;

import android.test.AndroidTestCase;
import android.test.InstrumentationTestCase;

import com.example.lyl.mobilesafe.dao.BlackListDAO;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest{
    @Test
    public void addition_isCorrect() throws Exception {
        List<Integer> a = new ArrayList<Integer>();
        a.add(1);
        a.add(2);
        a.add(1);
        a.add(3);
        a.add(1);
        a.add(4);
        a.add(1);
        a.add(3);
        a.add(1);
        List<Integer> b = new ArrayList<Integer>();
        for(int i : a){
            if( i == 1){
                continue;
            }
            b.add(i);
        }
        System.out.println(b);
    }

}