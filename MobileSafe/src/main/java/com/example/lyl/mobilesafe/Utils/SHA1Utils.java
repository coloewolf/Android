package com.example.lyl.mobilesafe.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Created by lyl on 2016/7/7.
 *
 */
public class SHA1Utils {

    /**
     * 得到文件的sha1码
     * @param path 文件路径
     * @return 文件的sha1码
     */
    public static String getSHA1Code(String path){
        try {
            MessageDigest digest = MessageDigest.getInstance("sha-1");
            FileInputStream fis = new FileInputStream(new File(path));
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = fis.read(buf)) != -1){
                digest.update(buf, 0, len);
            }
            StringBuffer sb = new StringBuffer();
            byte[] result = digest.digest();
            for(byte b : result){
                String str = Integer.toHexString(b & 0xff);
                if( str.length() == 1){
                    sb.append("0");
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


}
