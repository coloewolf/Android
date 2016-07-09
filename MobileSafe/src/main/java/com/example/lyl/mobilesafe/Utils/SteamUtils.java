package com.example.lyl.mobilesafe.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by lyl on 2016/6/27.
 *
 */
public class SteamUtils {

    /**
     * 把一个流的内容读取出来转成字符串
     * @param is 输入流
     * @return 如果解析成功返回字符串 如果解析失败返回Null
     */
    public static String readSteam(InputStream is){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int len = 0;
            while((len = is.read(buf)) != -1){
                baos.write(buf,0,len);
            }
            is.close();
            return baos.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
