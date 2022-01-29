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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.databinding.FragmentBearingBushMonitor1Binding;
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

public class BearingBushMonitor1 extends Fragment {
    private FragmentBearingBushMonitor1Binding binding;
    private static Handler handler;
    private final ArrayList<Integer> colorsA = new ArrayList<>();
    private final ArrayList<Integer> colorsB = new ArrayList<>();
    private double limitTop = 15;
    private double warnTop = 12;
    private double limitBottom = -15;
    private double warnBottom = -13;
    private Timer timer;
    private BearingBushDevice device;

    public BearingBushMonitor1() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BearingBushMonitorActivity activity = (BearingBushMonitorActivity)getActivity();
        if (activity != null) this.device = activity.getDevice();
        if (this.device != null) {
            this.limitTop = device.getFuhebaojing();
            this.limitBottom = device.getFeifuhebaojing();
            this.warnTop = device.getFuheyujing();
            this.warnBottom = device.getFeifuheyujing();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_bearing_bush_monitor1, container, false );
        // Inflate the layout for this fragment
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
                    JSONArray array1 = data.getJSONArray("a");
                    JSONArray array2 = data.getJSONArray("b");
                    updateChart1(array1, array2);
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
        timer.schedule(new BearingBushMonitor1.myTask(), 0, 5000);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    private void initChat1() {
        binding.monitor1.getLegend().setEnabled(false);
        binding.monitor1.getDescription().setEnabled(false);
        binding.monitor1.setDoubleTapToZoomEnabled(false);
        binding.monitor1.setPinchZoom(false);
        //  binding.monitor1.setOnTouchListener(this);

        XAxis xAxis = binding.monitor1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(15f);

        binding.monitor1.getAxisRight().setEnabled(false);
        YAxis leftAxis = binding.monitor1.getAxisLeft();
        leftAxis.setSpaceTop(10f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMaximum((float) device.getFuhea());
        leftAxis.setAxisMinimum((float) device.getFeifuheb());
        leftAxis.setGranularity((float) device.getFuhea());

        LimitLine ll_limitTop = new LimitLine((float) limitTop);
        ll_limitTop.setLineColor(ContextCompat.getColor(getContext(), R.color.crimson));
        ll_limitTop.setLineWidth(1f);
        leftAxis.addLimitLine(ll_limitTop);
        LimitLine ll_warnTop = new LimitLine((float) warnTop);
        ll_warnTop.setLineColor(ContextCompat.getColor(getContext(), R.color.goldyellow));
        ll_warnTop.setLineWidth(1f);
        ll_warnTop.setLabel("A");
        ll_warnTop.setTextSize(15f);
        ll_warnTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        leftAxis.addLimitLine(ll_warnTop);

        LimitLine ll_limitBottom = new LimitLine((float) limitBottom);
        ll_limitBottom.setLineColor(ContextCompat.getColor(getContext(), R.color.crimson));
        ll_limitBottom.setLineWidth(1f);
        leftAxis.addLimitLine(ll_limitBottom);
        LimitLine ll_warnBottom = new LimitLine((float) warnBottom);
        ll_warnBottom.setLineColor(ContextCompat.getColor(getContext(), R.color.goldyellow));
        ll_warnBottom.setLineWidth(1f);
        ll_warnBottom.setLabel("B");
        ll_warnBottom.setTextSize(15f);
        ll_warnBottom.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        leftAxis.addLimitLine(ll_warnBottom);

        //图1中x轴，上半图值x>0（离x轴越远值越大），下半图值y<0（离x轴越远值越大）
        LimitLine ll_baseline = new LimitLine(0);
        ll_baseline.setLineColor(ContextCompat.getColor(getContext(), R.color.dark));
        ll_baseline.setLineWidth(0.5f);
        leftAxis.addLimitLine(ll_baseline);
    }

    private void updateChart1(JSONArray array1, JSONArray array2) {
        if (getActivity() == null) return;
        if (binding.monitor1.getData() != null && binding.monitor1.getData().getDataSetCount() > 0) {
            for (int i = 0; i < binding.monitor1.getData().getDataSetCount(); i++) {
                binding.monitor1.getData().removeDataSet(i);
            }
        }
        LineDataSet dataSet1 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.UP);
        LineDataSet dataSet2 = new MyLineDataSet(new ArrayList<>(), "", MyLineDataSet.Orientation.DOWN);
        colorsA.clear();
        colorsB.clear();
        try {
            for (int i = 0; i < array1.length() && i < array2.length(); i++) {
                float a = (float) array1.getDouble(i);
                float b = (float) array2.getDouble(i);
                dataSet1.addEntry(new Entry(dataSet1.getEntryCount(), a));
                dataSet2.addEntry(new Entry(dataSet2.getEntryCount(), b));
                binding.distanceA.setText(String.valueOf(a));
                binding.distanceB.setText(String.valueOf(b));
                if (a >= limitTop) {
                    colorsA.add(Color.RED);
                } else if (a < limitTop && a >= warnTop) {
                    colorsA.add(Color.rgb(255, 148, 40));
                } else {
                    colorsA.add(Color.GREEN);
                }

                if (b <= limitBottom) {
                    colorsB.add(Color.RED);
                } else if (b > limitBottom && b <= warnBottom) {
                    colorsB.add(Color.rgb(255, 148, 40));
                } else {
                    colorsB.add(Color.GREEN);
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
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class myTask extends TimerTask{
        @Override
        public void run() {
            Utils.getBearingBushDeviceAB(handler, device.getId());
        }
    }
}