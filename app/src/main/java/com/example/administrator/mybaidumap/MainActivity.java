package com.example.administrator.mybaidumap;
/**作者：夏晶
 * 时间：2017.03
 * 功能：实现实时轨迹显示，历史轨迹查询以及拍摄视频与定位位置相关联功能
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.model.LocationMode;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.TraceLocation;
import com.example.administrator.mybaidumap.db.Point;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static java.lang.Math.sqrt;


public class MainActivity extends Activity {
    private static final String TAG = "MainActivity";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private MapView mMapView = null;
    private BaiduMap mBaiduMap = null;
    private Button switchButton;   //切换地图样式按钮
    private Button recordButton;   //录视频按钮
    private Button startTrackButton;   //开始记录轨迹按钮
    private Button searchButton;    //搜寻历史轨迹按钮
    private boolean switchStyleFlag = true;
    private boolean historyShow = true;
    private boolean isFirstTrace = true;
    private Uri fileUri;
    private EditText editText;
    private EditText editText2;
    public LocationClient mLocClient = null;

    private static OnTraceListener startTraceListener = null;
    private static OnEntityListener entityListener = null;
    private RefreshThread refreshThread = null;
    private static BitmapDescriptor realtimeBitmap = null;
    private static BitmapDescriptor videoPointBitmap = null;
    private static OverlayOptions overlay; //起始点图标overlay
    private static PolylineOptions polyline;
    private List<LatLng> pointList = new ArrayList<>();
    private LBSTraceClient client;



    private boolean isFirstLoc = true;  //是否首次定位
    private long startTime; //记录轨迹开始时间
    private long endTime;   //终止时间
    private int groupId;




    public BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //mMapView 销毁后不再处理新接收的位置信息
            if(bdLocation == null || mBaiduMap == null) return;
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(100).latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            if(isFirstLoc){
                isFirstLoc = false;
                //设置地图中心点以及缩放级别
                LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll,18);
                mBaiduMap.animateMapStatus(u);
               /* BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka4);
                 OverlayOptions option = new MarkerOptions()
                      .position(ll)
                     .icon(mCurrentMarker);
                  mBaiduMap.addOverlay(option); */
                //获取定位结果
                StringBuffer sb = new StringBuffer(256);

                sb.append("time : ");
                sb.append(bdLocation.getTime());    //获取定位时间

                sb.append("\nerror code : ");
                sb.append(bdLocation.getLocType());    //获取类型类型

                sb.append("\nlatitude : ");
                sb.append(bdLocation.getLatitude());    //获取纬度信息

                sb.append("\nlontitude : ");
                sb.append(bdLocation.getLongitude());    //获取经度信息

                sb.append("\nradius : ");
                sb.append(bdLocation.getRadius());    //获取定位精准度

                if (bdLocation.getLocType() == BDLocation.TypeGpsLocation){

                    // GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(bdLocation.getSpeed());    // 单位：公里每小时

                    sb.append("\nsatellite : ");
                    sb.append(bdLocation.getSatelliteNumber());    //获取卫星数

                    sb.append("\nheight : ");
                    sb.append(bdLocation.getAltitude());    //获取海拔高度信息，单位米

                    sb.append("\ndirection : ");
                    sb.append(bdLocation.getDirection());    //获取方向信息，单位度

                    sb.append("\naddr : ");
                    sb.append(bdLocation.getAddrStr());    //获取地址信息

                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");

                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation){

                    // 网络定位结果
                    sb.append("\naddr : ");
                    sb.append(bdLocation.getAddrStr());    //获取地址信息

                    sb.append("\noperationers : ");
                    sb.append(bdLocation.getOperators());    //获取运营商信息

                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");

                } else if (bdLocation.getLocType() == BDLocation.TypeOffLineLocation) {

                    // 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");

                } else if (bdLocation.getLocType() == BDLocation.TypeServerError) {

                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，" +
                            "会有人追查原因");

                } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkException) {

                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");

                } else if (bdLocation.getLocType() == BDLocation.TypeCriteriaException) {

                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，" +
                            "处于飞行模式下一般会造成这种结果，可以试着重启手机");

                }

                sb.append("\nlocationdescribe : ");
                sb.append(bdLocation.getLocationDescribe());    //位置语义化信息

                List<Poi> list = bdLocation.getPoiList();    // POI数据
                if (list != null) {
                    sb.append("\npoilist size = : ");
                    sb.append(list.size());
                    for (Poi p : list) {
                        sb.append("\npoi= : ");
                        sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                    }
                }

                Log.i(TAG, sb.toString());
                Toast.makeText(getApplicationContext(),bdLocation.getAddrStr(),
                        Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {
        }
    };

    public BaiduMap.OnMarkerClickListener onMarkerClickListener = new BaiduMap.OnMarkerClickListener() {
        /**
         * 地图 Marker 覆盖物点击事件监听函数
         * @param marker 视频图标点击后，播放对应视频
         */
        public boolean onMarkerClick(Marker marker){
            System.out.println("onMarkerClick: 有有。。。。。1");

            LatLng markerPoint = marker.getPosition();
            try {
                List <Point> points = DataSupport.findAll(Point.class);
                if (points.size()>=1) {
                    for(Point point : points){
                        if(point.getLatitude()==markerPoint.latitude && point.getLongitude()==markerPoint.longitude){
                            if(point.getStartOrEnd() == 1 || point.getStartOrEnd() == 2) {
                                Intent intent = new Intent(MainActivity.this, DialogActivity.class);
                                intent.putExtra("pointDate", point.getDate().toString());
                                startActivity(intent);
                            }
                            if (!point.getSavePath().equals("")) {
                                Intent intent = new Intent(MainActivity.this,PlayActivity.class);
                                intent.putExtra("filename",point.getSavePath());
                                startActivity(intent);

                            }


                            break;
                        }

                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());  //  In this way the VM ignores the file URI exposure.
        init(); //相关变量初始化
        initListener(); //初始化监听器
        LitePal.getDatabase();  //建立数据库
        
        
    }

    private void init(){

        mMapView = (MapView) findViewById(R.id.bmapView);
        switchButton = (Button) findViewById(R.id.switch_btn);  //地图样式按钮
        recordButton = (Button) findViewById(R.id.record_btn);   //录视频按钮
        startTrackButton = (Button) findViewById(R.id.start_btn);    //记录轨迹按钮
        searchButton = (Button) findViewById(R.id.search_btn);
        editText = (EditText) findViewById(R.id.searchResult_bar);
        editText2 = (EditText) findViewById(R.id.searchResult_bar2);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);
        mLocClient = new LocationClient(getApplicationContext()); //实例化LocationClient类
        mLocClient.registerLocationListener(myListener);  //注册监听函数
        this.setLocationOption();
        mLocClient.start();

        //int gatherInterval = 1; //定位周期，秒
        //String entityName = getImei(getApplicationContext());
        client = new LBSTraceClient(getApplicationContext());
        client.setLocationMode(LocationMode.Device_Sensors);
        //trace = new Trace(serviceId, entityName,false);  //实例化轨迹服务
        //client.setInterval(gatherInterval, packInterval);  //设置位置采集和打包周期
        //client.startTrace(trace, startTraceListener);
        realtimeBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_start);
        //纠偏选项

        prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    }


    private void initListener(){
        /**
         * 切换地图样式（附加清除数据）
         */
        switchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (switchStyleFlag) {
                    DataSupport.deleteAll(Point.class);
                    editor = prefs.edit();
                    editor.clear();
                    editor.apply();
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                    switchStyleFlag = false;
                } else {
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                    switchStyleFlag = true;
                }

            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (historyShow) {
                    historyShow = false;
                    String content = editText.getText().toString(); //获取编辑框1的内容
                    String content2 = editText2.getText().toString();   //获取编辑框2的内容
                    List <Point> allPoints = DataSupport.findAll(Point.class);
                    List<Point> historyPoints = new ArrayList<Point>();
                    if (!content.isEmpty() && !content2.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");    //将内容转换为时间
                        try {
                            startTime = sdf.parse(content).getTime()/1000+12*3600;
                            endTime = sdf.parse(content2).getTime()/1000+12*3600;
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(),"格式应为：2017010101",
                                    Toast.LENGTH_SHORT).show();
                        }
                        for(Point point : allPoints){
                            long time = point.getDate().getTime();
                            if(time>=startTime && time<=endTime){//遍历所有满足条件的point
                                historyPoints.add(point);
                                if(point.getSavePath() != null){  //判断该点是否与某视频相关联
                                    LatLng latLng = new LatLng(point.getLatitude(),point.getLongitude());
                                    videoPointBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_video);
                                    overlay = new MarkerOptions().position(latLng)
                                            .icon(videoPointBitmap).zIndex(9).draggable(true);
                                    mBaiduMap.addOverlay(overlay);
                                }
                            }

                        }
                        drawHistoryTrack(historyPoints);

                    } else{
                        Toast.makeText(getApplicationContext(),"请输入完整日期！",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    historyShow = true;
                    editText.setText("");
                    editText2.setText("");
                    mBaiduMap.clear();
                }
            }
        });
        /**
         * 地图点击，标注显示
         */
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {
                //在此处理点击事件
            }

            public boolean onMapPoiClick(MapPoi poi) {
                /*
                //在此处理底图标注点击事件
                String poiName = poi.getName();
                LatLng poiPosition = poi.getPosition();
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_marka3);
                OverlayOptions option = new MarkerOptions()
                        .position(poiPosition)
                        .icon(bitmap);
                mBaiduMap.addOverlay(option);
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(poiPosition));
                Toast.makeText(getApplicationContext(), poiName, Toast.LENGTH_SHORT).show();
                */
                return false;
            }
        });
        /**
         * 录制视频按钮监听
         */
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                fileUri = getOutputMediaFileUri();  // create a file to save the icon_video
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the icon_video image quality to high
                // start the Video Capture Intent
                startActivityForResult(intent, 1);
            }

        });
        /**
         * 实时显示轨迹按钮监听
         */
        startTrackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFirstTrace) {
                    //判断是否有groupId缓存数据，有就提取并加一，否则赋值为1（第一次创建活动时）。

                    if (prefs.contains("groupId")) {
                        groupId = prefs.getInt("groupId", 0) + 1;
                    } else {
                        groupId = 1;
                        editor = prefs.edit();
                        editor.putInt("groupId", groupId);
                        editor.apply();
                    }
                    //开始实时显示轨迹
                    startRefreshThread(true);
                    mBaiduMap.clear();
                    isFirstTrace = false;
                    //client.startGather(startTraceListener);


                } else {
                    editor = prefs.edit();
                    editor.clear();
                    editor.putInt("groupId", groupId);
                    editor.apply();
                    //结束显示实时轨迹
                    isFirstTrace = true;
                    pointList.clear();
                    mBaiduMap.clear();
                    //client.stopGather(startTraceListener);
                    startRefreshThread(false);
                    Point point = DataSupport.findLast(Point.class);//标记为终点
                    point.setStartOrEnd(2);
                    point.save();

                }
            }
        });


        /**
         *  实体状态监听器
         */
        entityListener = new OnEntityListener(){

            @Override
//            接收最新的轨迹点
            public void onReceiveLocation(TraceLocation traceLocation) {
                super.onReceiveLocation(traceLocation);
                LatLng point = new LatLng(traceLocation.getLatitude(),traceLocation.getLongitude());

                if(pointList.size()==0){
                    overlay = new MarkerOptions().position(point)
                            .icon(realtimeBitmap).zIndex(9).draggable(false);
                    mBaiduMap.addOverlay(overlay);
                    pointList.add(point);
                    Date date = new Date(System.currentTimeMillis()/1000);
                    Point point1 = new Point();  //Point表数据
                    point1.setDate(date);
                    point1.setLatitude(point.latitude);
                    point1.setLongitude(point.longitude);
                    point1.setGroupId(groupId);
                    point1.setStartOrEnd(1);  //标记为起点
                    point1.save();
                }else {
                    LatLng last = pointList.get(pointList.size() - 1);
                    double distance = getDistance(point,last);
                    if (distance < 80 && distance>0.1) {
                        Toast.makeText(getApplicationContext(),"夏树让："+distance,Toast.LENGTH_SHORT).show();
                        pointList.add(point);
                        drawRealtimePoint(point,last);
                        Date date = new Date(System.currentTimeMillis()/1000);
                        Point point1 = new Point();  //Point表数据
                        point1.setDate(date);
                        point1.setLatitude(point.latitude);
                        point1.setLongitude(point.longitude);
                        point1.setGroupId(groupId);
                        point1.save();

                        Log.d(TAG, "onReceiveLocation: groupId-----" + groupId);

                    }
                }

            }
        };
    }
    /**
     * 计算两点之间的距离
     */
    public static double getDistance(LatLng point1,LatLng point2)
    {
        double lat1 = point1.latitude*100000;
        double lng1 = point1.longitude*100000;
        double lat2 = point2.latitude*100000;
        double lng2 = point2.longitude*100000;
        return sqrt((lat1-lat2)*(lat1-lat2)+(lng1-lng2)*(lng1-lng2));
    }

    /**
     *  追踪开始
     */
    
    private class RefreshThread extends Thread{

        protected boolean refresh = true;
        private int packInterval = 3;
        public void run(){

            while(refresh){
                queryRealtimeTrack();
                System.out.println("线程更新"+pointList.size());
                try{
                    Thread.sleep(packInterval * 1000);
                }catch(InterruptedException e){
                    System.out.println("线程休眠失败");
                }
            }

        }
    }

    /**
     * 查询实时线路
     */
    private void queryRealtimeTrack(){
        long serviceId = 141540;
        LocRequest locRequest = new LocRequest(1,serviceId);
        client.queryRealTimeLoc(locRequest,entityListener);

    }

    /**
     * 启动刷新线程
     * @param isStart
     */
    private void startRefreshThread(boolean isStart){

        if(refreshThread == null){
            refreshThread = new RefreshThread();
        }

        refreshThread.refresh = isStart;

        if(isStart){
            if(!refreshThread.isAlive()){
                refreshThread.start();
            }
        } else{
            refreshThread = null;
        }

    }

    /**
     * 画出实时线路点
     * @param point
     */
    private void drawRealtimePoint(LatLng point,LatLng last){

//           每次画两个点
        List<LatLng> latLngs = new ArrayList<LatLng>();
        latLngs.add(last);
        latLngs.add(point);
        polyline = new PolylineOptions().width(10).color(Color.BLUE).points(latLngs);
        mBaiduMap.addOverlay(polyline);

    }

    private void drawHistoryTrack(List<Point>pointList){

        int listSize = pointList.size();
        if (listSize > 0) {

            int firstGroupId = pointList.get(0).getGroupId();
            int lastGroupId = pointList.get(listSize-1).getGroupId();
            for(int i = firstGroupId; i<= lastGroupId; i++){
                List<Point> points = DataSupport.where("groupId = ?",String.valueOf(i)).find(Point.class);
                if (points.size() > 1) {
                    drawHistoryByGroup(points);
                }
            }
        } else {
            Toast.makeText(getApplicationContext(),"该时区未记录轨迹",Toast.LENGTH_SHORT).show();
        }
    }

    private void drawHistoryByGroup(List<Point> points) {
        List<LatLng> latlngs = new ArrayList<>();
        BitmapDescriptor startPointBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_start);
        BitmapDescriptor endPointBitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_end);
        for(Point point : points){
            LatLng ll = new LatLng(point.getLatitude(),point.getLongitude());
            latlngs.add(ll);
            if(point.getStartOrEnd() == 1){
                LatLng start = new LatLng(point.getLatitude(),point.getLongitude());
                overlay = new MarkerOptions().position(start)
                        .icon(startPointBitmap).zIndex(9).draggable(false);
                mBaiduMap.addOverlay(overlay);
            } else if(point.getStartOrEnd() == 2){
                LatLng end = new LatLng(point.getLatitude(),point.getLongitude());
                overlay = new MarkerOptions().position(end)
                        .icon(endPointBitmap).zIndex(9).draggable(false);
                mBaiduMap.addOverlay(overlay);

            }
        }
        polyline = new PolylineOptions().width(15).color(Color.BLUE).points(latlngs);
        mBaiduMap.addOverlay(polyline);
    }

    /**
     *  获取手机识别码
     */
/*
    private String getImei(Context context){
        String mImei = "NULL";
        try {
            mImei = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        } catch (Exception e) {
            mImei = "NULL";
        }
        return mImei;
    }
*/
    /**
     * 设置定位选项
     */
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true);  //打开GPS
        option.setLocationMode(LocationClientOption.LocationMode.Device_Sensors); //设置定位模式
        option.setCoorType("bd09ll"); //返回的定位结果是百度经纬度默认值gcj02
        option.setScanSpan(2000);  //设置发起定位请求的间隔时间为2000ms
        //option.setOpenAutoNotifyMode(); //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化
        // 就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        option.setIsNeedAddress(true);  //返回的定位结果包含地址信息
        option.setNeedDeviceDirect(true);  //返回的定位结果包含手机机头的方向
        mLocClient.setLocOption(option);

    }
    /**视频录制成功之后的反馈*/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode){
            case 1:
                if (resultCode == RESULT_OK) {
                    // Video captured and saved to fileUri specified in the Intent
                    Toast.makeText(this, "Video saved to:\n" +
                            data.getData(), Toast.LENGTH_SHORT).show();
                    int size = pointList.size();
                    if (size>=1) {
                        //               在最新轨迹点上添加视频图标
                        LatLng latLng = new LatLng(pointList.get(size-1).latitude,pointList.get(size-1).longitude);
                        videoPointBitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_video);
                        overlay = new MarkerOptions().position(latLng)
                                .icon(videoPointBitmap).zIndex(7).draggable(false);
                        mBaiduMap.addOverlay(overlay);
                        String savePath = data.getData().toString();
                        //录视频后，赋予最新点视频存储路径
                        Point point = DataSupport.findLast(Point.class);
                        point.setSavePath(savePath);
                        point.save();
                    }else {
                        Toast.makeText(getApplicationContext(),"未开始记轨！",Toast.LENGTH_SHORT).show();
                    }

                } else if (resultCode == RESULT_CANCELED) {
                    // User cancelled the icon_video capture
                    Toast.makeText(this,"已退出", Toast.LENGTH_SHORT).show();
                } else {
                    // Video capture failed, advise user
                    Toast.makeText(this,"请重试",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }


    }
    /** Create a file Uri for saving an image or icon_video */
    private static Uri getOutputMediaFileUri(){
        return Uri.fromFile(getOutputMediaFile());
    }
    /** Create a File for saving an image or icon_video */
    private static File getOutputMediaFile(){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.
        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }
        // Create a media file name
        @SuppressLint("SimpleDateFormat") String timeStamp = new
                SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;

        mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "VID_"+ timeStamp + ".mp4");

        return mediaFile;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mMapView = null;
        mLocClient.stop();
        //client.stopTrace(trace,startTraceListener);
        //DataSupport.deleteAll(Point.class);
        //editor.clear();

    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

}
