package com.baowu.zwyuser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.bean.BearingDevice;
import com.baowu.zwyuser.bean.SelectorItem;
import com.baowu.zwyuser.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Arrays;

import static com.baowu.zwyuser.MyApplication.NEW_ALARM;
import static com.baowu.zwyuser.MyApplication.USER_SP;
import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;
import static com.baowu.zwyuser.Utils.LIST;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        init();
        SharedPreferences sp = getApplication().getSharedPreferences(USER_SP, Context.MODE_PRIVATE);
        if (!sp.getBoolean(NEW_ALARM, false)) alert();
        Utils.querySiteByUserId(handler, Utils.getUserId(getApplication()));
        binding.spinnerSite.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SelectorItem site = (SelectorItem) adapterView.getSelectedItem();
                int siteId = site.getId();
                Utils.queryDeviceBySiteId(handler, siteId);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.query.setOnClickListener(view -> {
            SelectorItem device = (SelectorItem) binding.spinnerDevice.getSelectedItem();
            if (device == null) return;
            int deviceId = device.getId();
            Utils.queryConfiguration(handler, deviceId);
        });
        binding.back.setOnClickListener(view -> onBackPressed());
    }

    private void init() {
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                ArrayList<SelectorItem> items = msg.getData().getParcelableArrayList(LIST);
                switch (msg.what) {
                    case Utils.SITE_FLAG:
                        Utils.initSpinnerWithSelectorItem(MainActivity.this, items, binding.spinnerSite);
                        break;
                    case Utils.DEVICE_FLAG:
                        Utils.initSpinnerWithSelectorItem(MainActivity.this, items, binding.spinnerDevice);
                        break;
                    case Utils.BEARING:
                        BearingDevice device = (BearingDevice) msg.getData().getSerializable(DEVICE_CONFIGURATION);
                        Intent intent = new Intent(MainActivity.this, BearingMonitorActivity.class);
                        intent.putExtra(DEVICE_CONFIGURATION, device);
                        startActivity(intent);
                        break;
                    case Utils.BEARING_BUSH:
                        BearingBushDevice device1 = (BearingBushDevice) msg.getData().getSerializable(DEVICE_CONFIGURATION);
                        Intent intent1 = new Intent(MainActivity.this, BearingBushMonitorActivity.class);
                        intent1.putExtra(DEVICE_CONFIGURATION, device1);
                        startActivity(intent1);
                        break;
                }
            }
        };
    }

    private void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("有报警信息，是否前往查看");
        builder.setPositiveButton("前往", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, AlarmListActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}