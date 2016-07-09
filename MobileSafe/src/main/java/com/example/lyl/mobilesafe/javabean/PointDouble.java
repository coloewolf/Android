package com.example.lyl.mobilesafe.javabean;

/**
 * Created by lyl on 2016/6/29.
 * 坐标 x经度 y纬度
 */
public class PointDouble {
    private double x, y;

    public PointDouble(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public String toString() {
        return "经度:" + x + ", 纬度:" + y;
    }
}
