package com.baowu.zwyuser;

import static com.baowu.zwyuser.AlarmInfoActivity.BEARING_BUSH_TYPE;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_ID;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_TYPE;
import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.databinding.FragmentBearingBushMonitor3Binding;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BearingBushMonitor3 extends Fragment {
    private FragmentBearingBushMonitor3Binding binding;
    private Handler handler;
    private final ArrayList<Integer> colors = new ArrayList<>();
    private final ArrayList<Integer> colorsNon = new ArrayList<>();
    private double limitTop1 = 15;
    private double limitTop2 = -15;
    private final int[] colorPalette1 = new int[]{R.color.crimson, R.color.goldyellow, R.color.dodgerblue};
    private Timer timer;
    private BearingBushDevice device;

    public BearingBushMonitor3() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BearingBushMonitorActivity activity = (BearingBushMonitorActivity)getActivity();
        if (activity != null) this.device = activity.getDevice();
        if (this.device != null) {
            this.limitTop1 = device.getFuhecezhoubaojing();
            this.limitTop2 = device.getFeifuhecezhoubaojing();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bearing_bush_monitor3, container, false );
        if (device != null) {
            String s = "设备：" + device.getName();
            binding.deviceName.setText(s);
        }
        initChart1();
        initChart2();
        binding.modifyArg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DisplayConfigurationActivity.class);
            intent.putExtra(DEVICE_CONFIGURATION, this.device);
            startActivity(intent);
        });
        binding.queryAlarm.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AlarmInfoActivity.class);
            if (device != null) {
                intent.putExtra(DEVICE_ID, device.getId());
                intent.putExtra(DEVICE_TYPE, BEARING_BUSH_TYPE);
            }
            startActivity(intent);
        });
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString(Utils.RESPONSE);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray array1 = data.getJSONArray("positive");
                    JSONArray array2 = data.getJSONArray("negative");
                    updateChart(array1, array2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(new BearingBushMonitor3.myTask(), 0, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void initChart1() {
        binding.monitor3.getLegend().setEnabled(false);
        binding.monitor3.getDescription().setEnabled(false);
        binding.monitor3.setDoubleTapToZoomEnabled(false);
        binding.monitor3.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor3.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        binding.monitor3.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor3.getAxisLeft();
        leftAxis.setGranularity((float) limitTop1);
        leftAxis.setSpaceTop(10f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) (Math.abs(limitTop1) * 1.2));
        leftAxis.setAxisMinimum((float) -(Math.abs(limitTop1) * 1.2));

        LimitLine ll_limitTop = new LimitLine((float) limitTop1);
        ll_limitTop.setLineColor(ContextCompat.getColor(getContext(), R.color.crimson));
        ll_limitTop.setLineWidth(1f);
        leftAxis.addLimitLine(ll_limitTop);

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(getContext(), R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        leftAxis.addLimitLine(ll_baseline);
    }

    private void initChart2() {
        binding.monitor4.getLegend().setEnabled(false);
        binding.monitor4.getDescription().setEnabled(false);
        binding.monitor4.setDoubleTapToZoomEnabled(false);
        binding.monitor4.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor4.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);

        binding.monitor4.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor4.getAxisLeft();
        leftAxis.setGranularity((float) limitTop2);
        leftAxis.setSpaceTop(10f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) (Math.abs(limitTop2) * 1.2));
        leftAxis.setAxisMinimum((float) -(Math.abs(limitTop2) * 1.2));

        LimitLine ll_limitTop = new LimitLine((float) limitTop2);
        ll_limitTop.setLineColor(ContextCompat.getColor(getContext(), R.color.crimson));
        ll_limitTop.setLineWidth(1f);
        leftAxis.addLimitLine(ll_limitTop);

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(getContext(), R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        leftAxis.addLimitLine(ll_baseline);
    }

    private void updateChart(JSONArray array1, JSONArray array2) {
        if (getActivity() == null) return;
        if (binding.monitor3.getData() != null && binding.monitor3.getData().getDataSetCount() > 0) {
            for (int i = 0; i < binding.monitor3.getData().getDataSetCount(); i++) {
                binding.monitor3.getData().removeDataSet(i);
            }
        }
        if (binding.monitor4.getData() != null && binding.monitor4.getData().getDataSetCount() > 0) {
            for (int i = 0; i < binding.monitor4.getData().getDataSetCount(); i++) {
                binding.monitor4.getData().removeDataSet(i);
            }
        }
        LineDataSet dataSet1 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.UP);
        LineDataSet dataSet2 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.DOWN);
        colors.clear();
        colorsNon.clear();
        try {
            for (int i = 0; i < array1.length() && i < array2.length(); i++) {
                float m = (float) array1.getDouble(i);
                float n = (float) array2.getDouble(i);
                dataSet1.addEntry(new BarEntry(dataSet1.getEntryCount(), m));
                dataSet2.addEntry(new BarEntry(dataSet2.getEntryCount(), n));
                binding.loadSideValue.setText(String.valueOf(m));
                binding.nonLoadSideValue.setText(String.valueOf(n));
                if (m >= Math.abs(limitTop1)) colors.add(Color.RED); else colors.add(Color.GREEN);
                if (n >= Math.abs(limitTop2)) colorsNon.add(Color.RED); else colorsNon.add(Color.GREEN);
                Utils.configureDataSet(dataSet1, colors);
                Utils.configureDataSet(dataSet2, colorsNon);
                LineData data1 = new LineData(dataSet1);
                binding.monitor3.setData(data1);
                binding.monitor3.getData().notifyDataChanged();
                binding.monitor3.notifyDataSetChanged();
                binding.monitor3.postInvalidate();
                LineData data2 = new LineData(dataSet2);
                binding.monitor4.setData(data2);
                binding.monitor4.getData().notifyDataChanged();
                binding.monitor4.notifyDataSetChanged();
                binding.monitor4.postInvalidate();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class myTask extends TimerTask {
        @Override
        public void run() {
            Utils.getBearingBushDeviceDS(handler, device.getId());
        }
    }
}