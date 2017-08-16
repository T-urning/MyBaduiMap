package com.example.administrator.mybaidumap;

import com.baidu.mapapi.model.LatLng;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Administrator on 2017/8/8.
 */

public class Point extends DataSupport {
    private int id;
    private java.util.Date date;
    private double latitude;
    private double longitude;
    private String savePath;
    private int groupId;
    private int startOrEnd;//1代表起点，2代表终点

    @Override
    public String toString() {
        return "Point{" +
                "id=" + id +
                ", date=" + date +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", savePath='" + savePath + '\'' +
                ", groupId=" + groupId +
                ", startOrEnd=" + startOrEnd +
                '}';
    }

    public int getStartOrEnd() {
        return startOrEnd;
    }

    public void setStartOrEnd(int startOrEnd) {
        this.startOrEnd = startOrEnd;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}
