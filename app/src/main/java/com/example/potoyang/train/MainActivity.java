package com.example.potoyang.train;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {

    private Button btn_loc, btn_get;
    private TextView tv_lon, tv_biaoji;

    private MapView mapView = null;
    private BaiduMap baiduMap = null;
    private LocationClient locationClient = null;
    private boolean isFirstLoc = true;

    private Utils checkGPS;

    private double mLatitude, mLongitude;
    private int windowWidth, windowHeight;

    private int headColor = 0, tailColor = 0;

    private static double[] latitude_array = {30.763698, 30.751588, 30.738796, 30.72749};
    private static double[] longitude_array = {103.978044, 103.98289, 103.98589, 104.00276};
    private static String[] station_name = {"犀浦站", "天河路", "百草路", "金周路"};
    private static String[] station_num = {"cd0101", "cd0102", "cd0103", "cd0104"};
    private Marker marker[];

    private View view_pop;
    private PopupWindow infoPopupWindow;
    private SwipeRefreshLayout swipeRefreshLayout;

    private PopAdapter popAdapter;

    private List<String> adapterData = new ArrayList<>();
    private List<Integer> colors = new ArrayList<>();

    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        checkGPS = new Utils(getBaseContext());
        locationClient = new LocationClient(getBaseContext());

        if (!checkGPS.isOpen(getBaseContext())) {
            new AlertDialog.Builder(this)
                    .setTitle("请开启GPS")
                    .setMessage("开启GPS后才能精确定位")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Utils.openGPS(getBaseContext());
                        }
                    })
                    .setCancelable(false)
                    .show();
        }

        mapView = (MapView) findViewById(R.id.bmapView);
        btn_loc = (Button) findViewById(R.id.btn_loc);
        btn_get = (Button) findViewById(R.id.btn_get);
        tv_lon = (TextView) findViewById(R.id.tv_lon);

        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        baiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        locationClient.setLocOption(Utils.setLocationOption());

        mapView.showZoomControls(false);
        locationClient.registerLocationListener(myListener);
        locationClient.start();

        //获取我的位置
        btn_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyLocation();

            }
        });

//        btn_get.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RequestParams params = new RequestParams();
//                params.put("id", 1);
//
//                AsyncHttpClient client = new AsyncHttpClient();
//                client.get("http://180.85.57.8:8888/Train/test.php", params,
//                        new AsyncHttpResponseHandler() {
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
//                                String s = new String(responseBody);
//                                latlon_array = s.split(",");
//
//                                ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption().location(
//                                        new LatLng(Double.parseDouble(latlon_array[0]), Double.parseDouble(latlon_array[1])));
//
//                                geoCoder.reverseGeoCode(reverseGeoCodeOption);
//
//                                geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
//                                    @Override
//                                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
//
//                                    }
//
//                                    @Override
//                                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
//                                        if (reverseGeoCodeResult == null || reverseGeoCodeResult.error != SearchResult.ERRORNO.NO_ERROR) {
//                                            return;
//                                        }
//                                        if (reverseGeoCodeResult != null && reverseGeoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
//
//                                            //得到位置
//                                            address = reverseGeoCodeResult.getAddress();
//                                            Toast.makeText(MainActivity.this, "" + address, Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
//
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
//
//                            }
//                        });
//            }
//        });

        final View view_biaoji = LayoutInflater.from(MainActivity.this).inflate(R.layout.biaoji, null);

        tv_biaoji = (TextView) view_biaoji.findViewById(R.id.tv_biaoji);

        marker = new Marker[4];
        for (int i = 0; i < 4; i++) {
            tv_biaoji.setText(station_name[i]);
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view_biaoji);
            LatLng latLngs = new LatLng(latitude_array[i], longitude_array[i]);
            OverlayOptions overlayMarker = new MarkerOptions()
                    .position(latLngs)
                    .icon(bitmap);
            marker[i] = (Marker) baiduMap.addOverlay(overlayMarker);
        }

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels;
        windowHeight = dm.heightPixels;

        baiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker m) {

                for (int index = 0; index < 4; index++) {
                    if (m == marker[index]) {
                        final int finalIndex = index;

                        view_pop = LayoutInflater.from(getBaseContext()).inflate(R.layout.pop, null);
                        TextView tv = (TextView) view_pop.findViewById(R.id.tv_pop);

                        tv.setText(station_name[index]);

                        list = (ListView) view_pop.findViewById(R.id.pop_list);

                        colors.clear();

                        for (int i = 0; i < 6; i++)
                            colors.add(Color.TRANSPARENT);

                        adapterData.clear();

                        adapterData.add("请下拉刷新");

                        headColor = Color.GREEN;
                        tailColor = Color.BLACK;

                        popAdapter = new PopAdapter(getBaseContext(), adapterData, headColor, tailColor, colors);

                        list.setAdapter(popAdapter);

                        infoPopupWindow = new PopupWindow(view_pop, windowWidth * 7 / 8, windowHeight * 5 / 8);
                        infoPopupWindow.showAtLocation(mapView, Gravity.CENTER, 0, 0);

                        swipeRefreshLayout = (SwipeRefreshLayout) view_pop.findViewById(R.id.sr_layout);
                        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.LTGRAY);
                        swipeRefreshLayout.setColorSchemeColors(Color.DKGRAY, Color.WHITE);

                        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                Message msg = new Message();
                                Bundle bundle = new Bundle();
                                bundle.putString("station_num", station_num[finalIndex]);
                                msg.setData(bundle);
                                handler.sendMessage(msg);
                            }
                        });

                    }
                }

                return true;
            }

        });


        baiduMap.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                tv_lon.setText(latLng.latitude + "   " + latLng.longitude);
            }
        });

    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bun = msg.getData();
            final String str = bun.getString("station_num");

            RequestParams params = new RequestParams();
            params.put("auth_key", str);
            AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://192.168.1.108:8888/Train/test.php", params,
                    new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String getFormServer = new String(responseBody);
                            HashMap<String, String> map = new HashMap<>();

                            try {
                                JSONTokener jsonParse = new JSONTokener(getFormServer);
                                JSONObject jsonArray = (JSONObject) jsonParse.nextValue();
                                map.put("data1", jsonArray.getString("data1"));
                                map.put("data2", jsonArray.getString("data2"));
                                map.put("data3", jsonArray.getString("data3"));
                                map.put("data4", jsonArray.getString("data4"));
                                map.put("data5", jsonArray.getString("data5"));
                                map.put("data6", jsonArray.getString("data6"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (!map.isEmpty()) {
                                if (map.get("data1").equals("null")) {
                                    adapterData.clear();
                                    popAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(MainActivity.this, "哦嚄,数据不见咯!", Toast.LENGTH_SHORT).show();
                                } else {
                                    adapterData.clear();
                                    adapterData.add(0, str);
                                    colors.add(0, Integer.valueOf(map.get("data1")));
                                    colors.add(1, Integer.valueOf(map.get("data2")));
                                    colors.add(2, Integer.valueOf(map.get("data3")));
                                    colors.add(3, Integer.valueOf(map.get("data4")));
                                    colors.add(4, Integer.valueOf(map.get("data5")));
                                    colors.add(5, Integer.valueOf(map.get("data6")));

                                    popAdapter.notifyDataSetChanged();
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            } else {
                                adapterData.clear();
                                popAdapter.notifyDataSetChanged();
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                        }
                    });

        }

    };

    // 返回到我的位置
    private void getMyLocation() {
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        baiduMap.animateMapStatus(msu);
    }

    private BDLocationListener myListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不再处理新接收的位置
            if (location == null || mapView == null)
                return;

            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            baiduMap.setMyLocationData(locData);//设置定位数据

            // 更新经纬度
            mLongitude = location.getLongitude();
            mLatitude = location.getLatitude();

            if (isFirstLoc) {
                isFirstLoc = false;

                LatLng latLng = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, 15); //设置地图中心点以及缩放级别
                baiduMap.animateMapStatus(u);
            }
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (infoPopupWindow != null && infoPopupWindow.isShowing()) {
            infoPopupWindow.dismiss();
            infoPopupWindow = null;
        }
        return super.onTouchEvent(event);
    }
}
