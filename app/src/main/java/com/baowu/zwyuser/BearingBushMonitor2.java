package com.baowu.zwyuser;

import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.databinding.FragmentBearingBushMonitor2Binding;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BearingBushMonitor2 extends Fragment {
    private FragmentBearingBushMonitor2Binding binding;
    private static Handler handler;
    private LineDataSet dataSet11;
    private LineDataSet dataSet12;
    private double limitTop = 15;
    private double limitBottom = -15;
    private final int[] colorPalette1 = new int[]{R.color.crimson, R.color.goldyellow, R.color.dodgerblue};
    private Timer timer;
    private BearingBushDevice device;

    public BearingBushMonitor2() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BearingBushMonitorActivity activity = (BearingBushMonitorActivity)getActivity();
        if (activity != null) this.device = activity.getDevice();
        if (this.device != null) {
            this.limitTop = device.getFeifuhebaojing();
            this.limitBottom = device.getFuhebaojing();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bearing_bush_monitor2, container, false );
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                String res = msg.getData().getString(Utils.RESPONSE);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    JSONObject data = jsonObject.getJSONObject("data");
                    JSONArray array1 = data.getJSONArray("c");
                    JSONArray array2 = data.getJSONArray("d");
                    updateChart1(array1, array2);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if (device != null) {
            String s = "设备：" + device.getName();
            binding.deviceName.setText(s);
        }
        initChat1();
        binding.modifyArg.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DisplayConfigurationActivity.class);
            intent.putExtra(DEVICE_CONFIGURATION, this.device);
            startActivity(intent);
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        timer.schedule(new BearingBushMonitor2.myTask(), 0, 5000);
    }

    private void initChat1() {
        //图1数据集(上半图)
        dataSet11 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.UP);
        dataSet11.setColor(Color.BLACK);
        dataSet11.setValueTextSize(12f);
        dataSet11.setDrawValues(false);
        //图1数据集(下半图)
        dataSet12 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.DOWN);
        dataSet12.setColor(Color.BLACK);
        dataSet12.setValueTextSize(12f);
        dataSet12.setDrawValues(false);
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet11);
        dataSets.add(dataSet12);
        LineData data1 = new LineData(dataSets);
        binding.monitor2.setData(data1);
        binding.monitor2.setData(data1);
        binding.monitor2.getLegend().setEnabled(false);
        binding.monitor2.getDescription().setEnabled(false);
        binding.monitor2.setDoubleTapToZoomEnabled(false);
        binding.monitor2.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor2.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(15f);

        binding.monitor2.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor2.getAxisLeft();
        leftAxis.setSpaceTop(10f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) device.getFuhea());
        leftAxis.setAxisMinimum((float) device.getFeifuheb());

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(getContext(), R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        ll_baseline.setLabel("C");
        ll_baseline.setTextSize(15f);
        ll_baseline.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        leftAxis.addLimitLine(ll_baseline);

        LimitLine ll_baseline1 = new LimitLine(0);
        ll_baseline1.setLineColor(ContextCompat.getColor(getContext(), R.color.dark));
        ll_baseline1.setLineWidth(0.5f);
        ll_baseline1.setLabel("D");
        ll_baseline1.setTextSize(15f);
        ll_baseline1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        leftAxis.addLimitLine(ll_baseline1);
    }

    private void updateChart1(JSONArray array1, JSONArray array2) {
        if (getActivity() == null) return;
        dataSet11.clear();
        dataSet12.clear();
        getActivity().runOnUiThread(() -> {
            try {
                binding.displacementC.setText(String.valueOf((float) array1.getDouble(array1.length() - 1)));
                binding.displacementD.setText(String.valueOf((float) array2.getDouble(array1.length() - 1)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
        try {
            for (int i = 0; i < array1.length() && i < array2.length(); i++) {
                dataSet11.addEntry(new BarEntry(dataSet11.getEntryCount(), (float) array1.getDouble(i)));
                dataSet12.addEntry(new BarEntry(dataSet12.getEntryCount(), (float) array2.getDouble(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        binding.monitor2.getData().notifyDataChanged();
        binding.monitor2.notifyDataSetChanged();
        binding.monitor2.postInvalidate();
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    class myTask extends TimerTask {
        @Override
        public void run() {
            Utils.getBearingBushDeviceCD(handler, device.getId());
        }
    }
}