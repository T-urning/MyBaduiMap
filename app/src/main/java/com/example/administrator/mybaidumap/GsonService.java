package com.example.administrator.mybaidumap;

import android.widget.Toast;

import com.google.gson.Gson;

public class GsonService {

    public static <T> T parseJson(String jsonString, Class<T> clazz) {
        T t = null;
        try {
            Gson gson = new Gson();
            t = gson.fromJson(jsonString, clazz);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("JSONaaaaaaaaaaaaaaaaaaaaaaaaaaaa啊啊啊啊啊啊");
        }
        return t;

    }
    public static String parseJson2(Object obj) {
        String s = "";
        try {
            Gson gson = new Gson();
            s = gson.toJson(obj);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println("JSONaaaaaaaaaaaaaaaaaaaaaaaaaaaa啊啊啊啊啊啊解析错误");
        }
        return s;

    }
}