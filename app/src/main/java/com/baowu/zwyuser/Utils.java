package com.baowu.zwyuser;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.baowu.zwyuser.bean.BearingAlarmInfo;
import com.baowu.zwyuser.bean.BearingBushAlarmInfo;
import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.bean.BearingDevice;
import com.baowu.zwyuser.bean.SelectorItem;
import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.baowu.zwyuser.MyApplication.USER_ID;
import static com.baowu.zwyuser.MyApplication.USER_SP;

import androidx.annotation.NonNull;

public class Utils {
    public static final int SITE_FLAG = 101;
    public static final int DEVICE_FLAG = 102;
    public static final String LIST = "LIST";
    public static final int BEARING= 110;
    public static final int BEARING_BUSH = 111;
    public static final String DEVICE_CONFIGURATION = "DEVICE_CONFIGURATION";
    public static final String BEARING_TYPE = "轴承";
    public static final int ALARM_BEARING_BUSH = 112;
    public static final int ALARM_BEARING = 113;
    public static final int BEARING_DEVICE_AB = 114;
    public static final int BEARING_BUSH_DEVICE_AB = 116;
    public static final int BEARING_BUSH_DEVICE_CD = 117;
    public static final int BEARING_BUSH_DEVICE_DS = 118;
    public static final String RESPONSE = "RESPONSE";
    public static final String BEARING_BUSH_RECORD = "BEARING_BUSH_RECORD";
    public static final String BEARING_RECORD = "BEARING_RECORD";

    public static int getUserId(Application application) {
        SharedPreferences sp = application.getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
        return sp.getInt(USER_ID, 0);
    }

    public static void initSpinner(Context context, List<String> array, Spinner spinner) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, array);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    public static  void configureTable (SmartTable table) {
        table.getConfig().setContentStyle(new FontStyle(60, Color.BLACK));
        table.getConfig().setTableTitleStyle(new FontStyle(80, Color.BLACK));
        table.getConfig().setColumnTitleStyle(new FontStyle(60, Color.BLACK));
        table.setZoom(true);
        table.getConfig().setShowTableTitle(false);
    }

    public static void querySiteByUserId(Handler handler, int id) {
        String url = "http://101.132.43.11:8001/user/info?id=" + id;
        ArrayList<SelectorItem> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray siteIds = data.getJSONArray("siteIds");
                    JSONArray sitNames  = data.getJSONArray("siteNames");
                    for (int i = 0; i < siteIds.length(); i++) {
                        String name = sitNames.getString(i);
                        int id = siteIds.getInt(i);
                        SelectorItem item = new SelectorItem(name, id);
                        list.add(item);
                    }
                    Message message = Message.obtain();
                    message.what = SITE_FLAG;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("LIST", list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("SiteLIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void queryDeviceBySiteId(Handler handler, int id) {
        String url = "http://101.132.43.11:8001/site/detail?siteId=" + id;
        ArrayList<SelectorItem> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray bearing = data.getJSONArray("zhouchengDeviceList");
                    JSONArray bearingBush = data.getJSONArray("zhouwaDeviceList");
                    for (int i = 0; i < bearing.length(); i++) {
                        String name = bearing.getJSONObject(i).getString("name");
                        int id = bearing.getJSONObject(i).getInt("id");
                        SelectorItem item = new SelectorItem(name, id);
                        list.add(item);
                    }
                    for (int i = 0; i < bearingBush.length(); i++) {
                        String name = bearingBush.getJSONObject(i).getString("name");
                        int id = bearingBush.getJSONObject(i).getInt("id");
                        SelectorItem item = new SelectorItem(name, id);
                        list.add(item);
                    }
                    Message message = Message.obtain();
                    message.what = DEVICE_FLAG;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("LIST", list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("DEVICELIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void initSpinnerWithSelectorItem(Context context, ArrayList<SelectorItem> list, Spinner spinner) {
        ArrayAdapter<SelectorItem> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, list);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(adapter);
    }

    public static void queryBearingBushAlarm(Handler handler, int id) {
        String url = "http://101.132.43.11:8001/zhouwadevice/alarm?deviceId=" + id;
        ArrayList<BearingBushAlarmInfo> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Log.d("RESPONSE", res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        BearingBushAlarmInfo info = new BearingBushAlarmInfo();
                        info.setData1(array.getJSONObject(i).getDouble("data1"));
                        info.setData2(array.getJSONObject(i).getDouble("data2"));
                        info.setData3(array.getJSONObject(i).getDouble("data3"));
                        info.setData4(array.getJSONObject(i).getDouble("data4"));
                        info.setData5(array.getJSONObject(i).getDouble("data5"));
                        info.setData6(array.getJSONObject(i).getDouble("data6"));
                        info.setAlarmDate(array.getJSONObject(i).getString("alarmDate"));
                        list.add(info);
                    }

                    Message message = Message.obtain();
                    message.what = ALARM_BEARING_BUSH;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(BEARING_BUSH_RECORD, list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("ALARMLIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void queryBearingAlarm(Handler handler, int id) {
        String url = "http://101.132.43.11:8001/zhouchengdevice/alarm?deviceId=" + id;
        ArrayList<BearingAlarmInfo> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                Log.d("RESPONSE", res);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        BearingAlarmInfo info = new BearingAlarmInfo();
                        info.setData1(array.getJSONObject(i).getDouble("data1"));
                        info.setData2(array.getJSONObject(i).getDouble("data2"));
                        info.setAlarmDate(array.getJSONObject(i).getString("alarmDate"));
                        list.add(info);
                    }

                    Message message = Message.obtain();
                    message.what = ALARM_BEARING;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList(BEARING_RECORD, list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("ALARMLIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void queryConfiguration(Handler handler, int id) {
        String url = "http://101.132.43.11:8001/device/detail?deviceId=" + id;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    Gson gson = new Gson();
                    JSONObject data = jsonObject.getJSONObject("data");
                    String type = data.getString("type");
                    if (BEARING_TYPE.equals(type)) {
                        BearingDevice device = gson.fromJson(String.valueOf(data), BearingDevice.class);
                        Message message = Message.obtain();
                        message.what = BEARING;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(DEVICE_CONFIGURATION, device);
                        message.setData(bundle);
                        handler.sendMessage(message);
                        Log.d("BEARING", gson.toJson(device));
                    } else {
                        BearingBushDevice device = gson.fromJson(String.valueOf(data), BearingBushDevice.class);
                        Message message = Message.obtain();
                        message.what = BEARING_BUSH;
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(DEVICE_CONFIGURATION, device);
                        message.setData(bundle);
                        handler.sendMessage(message);
                        Log.d("BEARING_BUSH", gson.toJson(device));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void queryBearingAlarmList(Handler handler, int userId) {
        String url = "http://101.132.43.11:8001/zhouchengdevice/alarm/list?userId=" + userId;
        ArrayList<SelectorItem> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray bearingBush = jsonObject.getJSONArray("data");
                    for (int i = 0; i < bearingBush.length(); i++) {
                        String name = bearingBush.getJSONObject(i).getString("deviceName");
                        int id = bearingBush.getJSONObject(i).getInt("deviceId");
                        SelectorItem item = new SelectorItem(name, id);
                        list.add(item);
                    }
                    Message message = Message.obtain();
                    message.what = DEVICE_FLAG;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("LIST", list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("DEVICELIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void queryBearingBushAlarmList(Handler handler, int userId) {
        String url = "http://101.132.43.11:8001/zhouwadevice/alarm/list?userId=" + userId;
        ArrayList<SelectorItem> list = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String res = response.body().string();
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONArray bearingBush = jsonObject.getJSONArray("data");
                    for (int i = 0; i < bearingBush.length(); i++) {
                        String name = bearingBush.getJSONObject(i).getString("deviceName");
                        int id = bearingBush.getJSONObject(i).getInt("deviceId");
                        SelectorItem item = new SelectorItem(name, id);
                        list.add(item);
                    }
                    Message message = Message.obtain();
                    message.what = DEVICE_FLAG;
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("LIST", list);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    Log.d("DEVICELIST", list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void getData(Handler handler, int type, String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String res = response.body().string();
                Message message = Message.obtain();
                message.what = type;
                Bundle bundle = new Bundle();
                bundle.putString(RESPONSE, res);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    public static void getBearingDeviceAB(Handler handler, int deviceId) {
        String url = "http://101.132.43.11:8001/zhouchengdevice/data/weiyi?deviceId=" + deviceId;
        Utils.getData(handler, BEARING_DEVICE_AB, url);
    }

    public static void getBearingBushDeviceAB(Handler handler, int deviceId) {
        String url = "http://101.132.43.11:8001/zhouwadevice/data/jianxi?deviceId=" + deviceId;
        Utils.getData(handler, BEARING_BUSH_DEVICE_AB, url);
    }

    public static void getBearingBushDeviceCD(Handler handler, int deviceId) {
        String url = "http://101.132.43.11:8001/zhouwadevice/data/weiyi?deviceId=" + deviceId;
        Utils.getData(handler, BEARING_BUSH_DEVICE_CD, url);
    }

    public static void getBearingBushDeviceDS(Handler handler, int deviceId) {
        String url = "http://101.132.43.11:8001/zhouwadevice/data/dingsheng?deviceId=" + deviceId;
        Utils.getData(handler, BEARING_BUSH_DEVICE_DS, url);
    }

    public static void configureDataSet(LineDataSet dataSet, ArrayList<Integer> colors) {
        dataSet.setColor(Color.BLACK);
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(false);
        dataSet.setCircleColors(colors);
    }

}
