package com.example.administrator.mybaidumap;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
public class RealtimeTrackData
{
    private int a;

    private int b;

    private List<C> c;  //entities

    private String message;

    private int status;

    private int tag;

    public void setA(int a){
        this.a = a;
    }
    public int getA(){
        return this.a;
    }
    public void setB(int b){
        this.b = b;
    }
    public int getB(){
        return this.b;
    }
    public void setC(List<C> c){
        this.c = c;
    }
    public List<C> getC(){
        return this.c;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setStatus(int status){
        this.status = status;
    }
    public int getStatus(){
        return this.status;
    }
    public void setTag(int tag){
        this.tag = tag;
    }
    public int getTag(){
        return this.tag;
    }

    public class C
    {
        private String a;

        private String c;

        private String d;

        private E e;

        private F f;
        public class E
        {
        }
        public class F  //EntityInfo
        {
            private String h;

            private int i;

            private K k;

            private A a;

            private String b;

            private int c;

            private int d;

            private int e;

            private int f;

            private int g;
            public class A  //latestLocation
            {
                private double latitude;

                private double longitude;

                public void setLatitude(double latitude){
                    this.latitude = latitude;
                }
                public double getLatitude(){
                    return this.latitude;
                }
                public void setLongitude(double longitude){
                    this.longitude = longitude;
                }
                public double getLongitude(){
                    return this.longitude;
                }
            }
            public class K
            {
            }

            public void setH(String h){
                this.h = h;
            }
            public String getH(){
                return this.h;
            }
            public void setI(int i){
                this.i = i;
            }
            public int getI(){
                return this.i;
            }
            public void setK(K k){
                this.k = k;
            }
            public K getK(){
                return this.k;
            }
            public void setA(A a){
                this.a = a;
            }
            public A getA(){
                return this.a;
            }
            public void setB(String b){
                this.b = b;
            }
            public String getB(){
                return this.b;
            }
            public void setC(int c){
                this.c = c;
            }
            public int getC(){
                return this.c;
            }
            public void setD(int d){
                this.d = d;
            }
            public int getD(){
                return this.d;
            }
            public void setE(int e){
                this.e = e;
            }
            public int getE(){
                return this.e;
            }
            public void setF(int f){
                this.f = f;
            }
            public int getF(){
                return this.f;
            }
            public void setG(int g){
                this.g = g;
            }
            public int getG(){
                return this.g;
            }
        }
        public void setA(String a){
            this.a = a;
        }
        public String getA(){
            return this.a;
        }
        public void setC(String c){
            this.c = c;
        }
        public String getC(){
            return this.c;
        }
        public void setD(String d){
            this.d = d;
        }
        public String getD(){
            return this.d;
        }
        public void setE(E e){
            this.e = e;
        }
        public E getE(){
            return this.e;
        }
        public void setF(F f){
            this.f = f;
        }
        public F getF(){
            return this.f;
        }

    }




    public LatLng getRealtimePoint() {
        if (c.get(0).f.a == null) {
            return null;
        }
        LatLng latLng = new LatLng(c.get(0).f.a.getLatitude(),
                c.get(0).f.a.getLongitude());
        System.out.println(c.size());
        if (Math.abs(latLng.latitude - 0.0) < 0.01 && Math.abs(latLng.longitude - 0.0) < 0.01) {
            return null;
        } else {
            return latLng;
        }
    }

}
