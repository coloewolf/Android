package com.example.lyl.mobilesafe.Utils;

import com.example.lyl.mobilesafe.javabean.PointDouble;

import java.io.InputStream;
import java.io.ObjectInputStream;

/**
 * Created by lyl on 2016/6/29.
 * 火星地球坐标转化工具类.地图坐标修偏
 */
public class ModifyOffsetUtils {
    private static ModifyOffsetUtils modifyOffsetUtils;
    static double[] X = new double[660 * 450];
    static double[] Y = new double[660 * 450];


    private ModifyOffsetUtils(InputStream inputStream) throws Exception {
        init(inputStream);
    }


    /**
     * 实例化工具类 需要数据库流
     * @param is 数据库流
     * @return 类的实例
     * @throws Exception
     */
    public synchronized static ModifyOffsetUtils getInstance(InputStream is) throws Exception {
        if (modifyOffsetUtils == null) {
            modifyOffsetUtils = new ModifyOffsetUtils(is);
        }
        return modifyOffsetUtils;
    }

    public void init(InputStream inputStream) throws Exception {
        ObjectInputStream in = new ObjectInputStream(inputStream);
        try {
            int i = 0;
            while (in.available() > 0) {
                if ((i & 1) == 1) {
                    Y[(i - 1) >> 1] = in.readInt() / 100000.0d;
                } else {
                    X[i >> 1] = in.readInt() / 100000.0d;
                }
                i++;
            }
        } finally {
            if (in != null)
                in.close();
        }
    }

    /**
     * standard -> china
     * @param pt PointDouble x经度 y纬度
     * @return PointDouble
     */
    public PointDouble s2c(PointDouble pt) {
        int cnt = 10;
        double x = pt.getX(), y = pt.getY();
        while (cnt-- > 0) {
            if (x < 71.9989d || x > 137.8998d || y < 9.9997d || y > 54.8996d)
                return pt;
            int ix = (int) (10.0d * (x - 72.0d));
            int iy = (int) (10.0d * (y - 10.0d));
            double dx = (x - 72.0d - 0.1d * ix) * 10.0d;
            double dy = (y - 10.0d - 0.1d * iy) * 10.0d;
            x = (x + pt.getX() + (1.0d - dx) * (1.0d - dy) * X[ix + 660 * iy] + dx
                    * (1.0d - dy) * X[ix + 660 * iy + 1] + dx * dy
                    * X[ix + 660 * iy + 661] + (1.0d - dx) * dy
                    * X[ix + 660 * iy + 660] - x) / 2.0d;
            y = (y + pt.getY() + (1.0d - dx) * (1.0d - dy) * Y[ix + 660 * iy] + dx
                    * (1.0d - dy) * Y[ix + 660 * iy + 1] + dx * dy
                    * Y[ix + 660 * iy + 661] + (1.0d - dx) * dy
                    * Y[ix + 660 * iy + 660] - y) / 2.0d;
        }
        return new PointDouble(x, y);
    }

    /**
     * china -> standard
     * @param pt PointDouble x经度 y纬度
     * @return PointDouble
     */
    public PointDouble c2s(PointDouble pt) {
        int cnt = 10;
        double x = pt.getX(), y = pt.getY();
        while (cnt-- > 0) {
            if (x < 71.9989d || x > 137.8998d || y < 9.9997d || y > 54.8996d)
                return pt;
            int ix = (int) (10.0d * (x - 72.0d));
            int iy = (int) (10.0d * (y - 10.0d));
            double dx = (x - 72.0d - 0.1d * ix) * 10.0d;
            double dy = (y - 10.0d - 0.1d * iy) * 10.0d;
            x = (x + pt.getX() - (1.0d - dx) * (1.0d - dy) * X[ix + 660 * iy] - dx
                    * (1.0d - dy) * X[ix + 660 * iy + 1] - dx * dy
                    * X[ix + 660 * iy + 661] - (1.0d - dx) * dy
                    * X[ix + 660 * iy + 660] + x) / 2.0d;
            y = (y + pt.getY() - (1.0d - dx) * (1.0d - dy) * Y[ix + 660 * iy] - dx
                    * (1.0d - dy) * Y[ix + 660 * iy + 1] - dx * dy
                    * Y[ix + 660 * iy + 661] - (1.0d - dx) * dy
                    * Y[ix + 660 * iy + 660] + y) / 2.0d;
        }
        return new PointDouble(x, y);
    }

}
