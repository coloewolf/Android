package com.example.lyl.mobilesafe.Utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by lyl on 2016/6/28.
 *
 */
public class Md5Utils {

    /**
     * 加密字符串采用md5算法
     * @param text 需要加密的文本
     * @return 加密后的文本字符串
     */
    public static String md5encode(String text,String salt){
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest((text+salt).getBytes());
            StringBuffer sb = new StringBuffer();
            for(byte b : result){
                String hex = Integer.toHexString(b&0xff);
                if(hex.length() == 1){
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            //不可能执行
            return "";
        }

    }
}
