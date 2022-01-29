package com.baowu.zwyuser;

import static android.os.Build.DEVICE;
import static com.baowu.zwyuser.AlarmInfoActivity.BEARING_TYPE;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_ID;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_TYPE;
import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.bean.BearingDevice;
import com.baowu.zwyuser.databinding.ActivityBearingMonitorBinding;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BearingMonitorActivity extends AppCompatActivity {
    private ActivityBearingMonitorBinding binding;
    private static Handler handler;
    private BearingDevice device;
    private final ArrayList<Integer> colorsA = new ArrayList<>();
    private final ArrayList<Integer> colorsB = new ArrayList<>();
    private final ArrayList<Integer> colorsC = new ArrayList<>();
    private final ArrayList<Integer> colorsD = new ArrayList<>();
    private Timer timer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bearing_monitor);
        device = (BearingDevice) getIntent().getSerializableExtra(DEVICE_CONFIGURATION);
        if (device != null) {
            String s = "设备：" + device.getName();
            binding.deviceName.setText(s);
        }
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.checkArg.setOnClickListener(v -> {
            Intent intent = new Intent(this, DisplayBearingConfigurationActivity.class);
            intent.putExtra(DEVICE_CONFIGURATION, this.device);
            startActivity(intent);
        });
        binding.queryAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmInfoActivity.class);
            if (device != null) {
                intent.putExtra(DEVICE_ID, device.getId());
                intent.putExtra(DEVICE_TYPE, BEARING_TYPE);
            }
            startActivity(intent);
        });
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString(Utils.RESPONSE);
                switch (msg.what) {
                    case Utils.BEARING_DEVICE_AB:
                        updateChart1(res);
                        updateChart2(res);
                }
            }
        };
        initChart1();
        initChart2();
    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(new BearingMonitorActivity.myTask(), 0, 10000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void initChart1() {
        binding.monitor1.getLegend().setEnabled(false);
        binding.monitor1.getDescription().setEnabled(false);
        binding.monitor1.setDoubleTapToZoomEnabled(false);
        binding.monitor1.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        binding.monitor1.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor1.getAxisLeft();
        leftAxis.setSpaceTop(10f);
        leftAxis.setGranularity((float) device.getDianjia());
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) device.getDianjia());
        leftAxis.setAxisMinimum((float) -device.getDianjia());

        LimitLine ll_limitTop = new LimitLine((float) device.getDianjibaojing());
        ll_limitTop.setLineColor(ContextCompat.getColor(this, R.color.crimson));
        ll_limitTop.setLineWidth(1f);
        ll_limitTop.setLabel("A(负荷侧)");
        ll_limitTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll_limitTop.setTextSize(15f);
        leftAxis.addLimitLine(ll_limitTop);

        LimitLine ll_limitBottom = new LimitLine((float) -device.getDianjibaojing());
        ll_limitBottom.setLineColor(ContextCompat.getColor(this, R.color.crimson));
        ll_limitBottom.setLineWidth(1f);
        ll_limitBottom.setLabel("B(非负荷侧)");
        ll_limitBottom.setTextSize(15f);
        leftAxis.addLimitLine(ll_limitBottom);

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(this, R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        leftAxis.addLimitLine(ll_baseline);
    }

    private void initChart2() {
        binding.monitor2.getLegend().setEnabled(false);
        binding.monitor2.getDescription().setEnabled(false);
        binding.monitor2.setDoubleTapToZoomEnabled(false);
        binding.monitor2.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        binding.monitor2.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor2.getAxisLeft();
        leftAxis.setGranularity((float) device.getJixiec());
        leftAxis.setSpaceTop(15f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) device.getJixiec());
        leftAxis.setAxisMinimum((float) -device.getJixiec());

        LimitLine ll_limitTop = new LimitLine((float) device.getJixiebaojing());
        ll_limitTop.setLineColor(ContextCompat.getColor(this, R.color.crimson));
        ll_limitTop.setLineWidth(1f);
        ll_limitTop.setLabel("C(负荷侧)");
        ll_limitTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll_limitTop.setTextSize(15f);
        leftAxis.addLimitLine(ll_limitTop);

        LimitLine ll_limitBottom = new LimitLine((float) -device.getJixiebaojing());
        ll_limitBottom.setLineColor(ContextCompat.getColor(this, R.color.crimson));
        ll_limitBottom.setLineWidth(1f);
        ll_limitBottom.setLabel("D(非负荷侧)");
        ll_limitBottom.setTextSize(15f);
        leftAxis.addLimitLine(ll_limitBottom);

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(this, R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        leftAxis.addLimitLine(ll_baseline);
    }

    private void updateChart1(String res) {
        if (binding.monitor1.getData() != null && binding.monitor1.getData().getDataSetCount() > 0) {
            for (int i = 0; i < binding.monitor1.getData().getDataSetCount(); i++) {
                binding.monitor1.getData().removeDataSet(i);
            }
        }
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray array1 = data.getJSONArray("a");
            JSONArray array2 = data.getJSONArray("b");
            LineDataSet dataSet1 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.UP);
            LineDataSet dataSet2 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.DOWN);
            colorsA.clear();
            colorsB.clear();
            for (int i = 0; i < array1.length() && i < array2.length(); i++) {
                float a = (float) array1.getDouble(i);
                float b = (float) array2.getDouble(i);
                dataSet1.addEntry(new Entry(dataSet1.getEntryCount(), a));
                dataSet2.addEntry(new Entry(dataSet2.getEntryCount(), b));
                if (a >= device.getDianjibaojing()) colorsA.add(Color.RED); else colorsA.add(Color.GREEN);
                if (b <= -device.getDianjibaojing()) colorsB.add(Color.RED); else colorsB.add(Color.GREEN);
                runOnUiThread(() -> {
                    binding.displacementA.setText(String.valueOf(a));
                    binding.displacementB.setText(String.valueOf(b));
                });
            }
            Utils.configureDataSet(dataSet1, colorsA);
            Utils.configureDataSet(dataSet2, colorsB);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet1);
            dataSets.add(dataSet2);
            LineData data1 = new LineData(dataSets);
            binding.monitor1.setData(data1);
            binding.monitor1.getData().notifyDataChanged();
            binding.monitor1.notifyDataSetChanged();
            binding.monitor1.postInvalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateChart2(String res) {
        if (binding.monitor2.getData() != null && binding.monitor2.getData().getDataSetCount() > 0) {
            for (int i = 0; i < binding.monitor2.getData().getDataSetCount(); i++) {
                binding.monitor2.getData().removeDataSet(i);
            }
        }
        try {
            JSONObject jsonObject = new JSONObject(res);
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray array1 = data.getJSONArray("a");
            JSONArray array2 = data.getJSONArray("b");
            LineDataSet dataSet1 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.UP);
            LineDataSet dataSet2 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.DOWN);
            colorsC.clear();
            colorsD.clear();
            for (int i = 0; i < array1.length() && i < array2.length(); i++) {
                float a = (float) array1.getDouble(i);
                float b = (float) array2.getDouble(i);
                dataSet1.addEntry(new Entry(dataSet1.getEntryCount(), a));
                dataSet2.addEntry(new Entry(dataSet2.getEntryCount(), b));
                if (a >= device.getJixiebaojing()) colorsC.add(Color.RED); else colorsC.add(Color.GREEN);
                if (b <= -device.getJixiebaojing()) colorsD.add(Color.RED); else colorsD.add(Color.GREEN);
                runOnUiThread(() -> {
                    binding.displacementC.setText(String.valueOf(a));
                    binding.displacementD.setText(String.valueOf(b));
                });
            }
            Utils.configureDataSet(dataSet1, colorsC);
            Utils.configureDataSet(dataSet2, colorsD);
            ArrayList<ILineDataSet> dataSets = new ArrayList<>();
            dataSets.add(dataSet1);
            dataSets.add(dataSet2);
            LineData data1 = new LineData(dataSets);
            binding.monitor2.setData(data1);
            binding.monitor2.getData().notifyDataChanged();
            binding.monitor2.notifyDataSetChanged();
            binding.monitor2.postInvalidate();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class myTask extends TimerTask {
        @Override
        public void run() {
            Utils.getBearingDeviceAB(handler, device.getId());
        }
    }
}
