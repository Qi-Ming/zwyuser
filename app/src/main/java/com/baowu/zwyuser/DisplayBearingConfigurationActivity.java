package com.baowu.zwyuser;

import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.bean.BearingDevice;
import com.baowu.zwyuser.databinding.ActivityDisplayBearingConfigurationBinding;

public class DisplayBearingConfigurationActivity extends AppCompatActivity {
    private ActivityDisplayBearingConfigurationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.finish.setOnClickListener(v -> onBackPressed());
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_bearing_configuration);
        Intent intent = getIntent();
        BearingDevice device = (BearingDevice) intent.getSerializableExtra(DEVICE_CONFIGURATION);
        if (device != null) {
            binding.site.setText(device.getSiteName());
            binding.deviceId.setText(device.getName());
            binding.type.setText(device.getType());
            binding.sensorX1.setText(String.valueOf(device.getDianjix1()));
            binding.sensorX2.setText(String.valueOf(device.getJixiex2()));
            binding.bearingArg1.setText(String.valueOf(device.getDianjia()));
            binding.bearingArg2.setText(String.valueOf(device.getDianjib()));
            binding.bearingArg3.setText(String.valueOf(device.getDianjibaojing()));
            binding.bearingArg4.setText(String.valueOf(device.getJixiec()));
            binding.bearingArg5.setText(String.valueOf(device.getJixied()));
            binding.bearingArg6.setText(String.valueOf(device.getJixiebaojing()));
        }
    }
}
