package com.example.administrator.mybaidumap;

import com.baidu.mapapi.model.LatLng;

import java.util.Date;

/**
 * Created by Administrator on 2017/4/19.
 */

public class VideoPoint {
    private java.util.Date date;
    private LatLng latLng;
    private String savePath;

    public VideoPoint(Date date, LatLng latLng) {
        this.date = date;
        this.latLng = latLng;
        this.savePath = "";
    }

    public VideoPoint(Date date, LatLng latLng, String savePath) {
        this.date = date;
        this.latLng = latLng;
        this.savePath = savePath;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    @Override
    public String toString() {
        return "VideoPoint{" +
                "date=" + date +
                ", latLng=" + latLng +
                ", savePath='" + savePath + '\'' +
                '}';
    }
}



