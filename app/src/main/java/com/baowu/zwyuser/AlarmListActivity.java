package com.baowu.zwyuser;

import static com.baowu.zwyuser.AlarmInfoActivity.BEARING_BUSH_TYPE;
import static com.baowu.zwyuser.AlarmInfoActivity.BEARING_TYPE;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_ID;
import static com.baowu.zwyuser.AlarmInfoActivity.DEVICE_TYPE;
import static com.baowu.zwyuser.MyApplication.NEW_ALARM;
import static com.baowu.zwyuser.MyApplication.USER_SP;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.bean.SelectorItem;
import com.baowu.zwyuser.databinding.ActivityAlarmListBinding;

import java.util.ArrayList;
import java.util.Arrays;

public class AlarmListActivity extends AppCompatActivity {
    private ActivityAlarmListBinding binding;
    private Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_alarm_list);
        changeStatus();
        final  String[] type = new String[] {"轴承式", "轴瓦式"};
        Utils.initSpinner(this, Arrays.asList(type), binding.spinnerType);
        binding.back.setOnClickListener(v -> onBackPressed());
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
//                ArrayList<SelectorItem> items = msg.getData().getParcelableArrayList(Utils.LIST);
                ArrayList<SelectorItem> items = new ArrayList<>();
                String type = (String) binding.spinnerType.getSelectedItem();
                if ("轴承式".equals(type)) {
                    SelectorItem item = new SelectorItem("Z-1", 907);
                    items.add(item);
                } else {
                    SelectorItem item = new SelectorItem("A-1", 713);
                    items.add(item);
                }
                if (msg.what == Utils.DEVICE_FLAG) {
                    Utils.initSpinnerWithSelectorItem(AlarmListActivity.this, items,
                            binding.spinnerDevice);
                }
            }
        };
        binding.spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String type = (String) binding.spinnerType.getSelectedItem();
                SharedPreferences sp = getApplication().getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
                int userId = sp.getInt("USER_ID",0);
                if ("轴承式".equals(type)) {
                   Utils.queryBearingAlarmList(handler,userId);
                } else {
                   Utils.queryBearingBushAlarmList(handler,userId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.query.setOnClickListener(v -> {
            Intent intent = new Intent(this, AlarmInfoActivity.class);
            String deviceType = (String) binding.spinnerType.getSelectedItem();
            SelectorItem device = (SelectorItem) binding.spinnerDevice.getSelectedItem();
            if (device == null) return;
            if ("轴瓦式".equals(deviceType)) {
                intent.putExtra(DEVICE_ID, device.getId());
                intent.putExtra(DEVICE_TYPE, BEARING_BUSH_TYPE);
            } else {
                intent.putExtra(DEVICE_ID, device.getId());
                intent.putExtra(DEVICE_TYPE, BEARING_TYPE);
            }
            startActivity(intent);
        });
    }

    private void changeStatus() {
        SharedPreferences sp = getApplication().getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(NEW_ALARM, true);
        editor.apply();
    }
}
