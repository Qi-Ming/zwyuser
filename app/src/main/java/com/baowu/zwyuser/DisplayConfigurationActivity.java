package com.baowu.zwyuser;


import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.databinding.ActivityDisplayConfigurationBinding;

public class DisplayConfigurationActivity extends AppCompatActivity {
    private ActivityDisplayConfigurationBinding binding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        binding.back.setOnClickListener(v -> onBackPressed());
        binding.finish.setOnClickListener(v -> onBackPressed());
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_display_configuration);
        Intent intent = getIntent();
        BearingBushDevice device = (BearingBushDevice) intent.getSerializableExtra(DEVICE_CONFIGURATION);
        if (device != null) {
            binding.site.setText(device.getSiteName());
            binding.type.setText(device.getType());
            binding.deviceId.setText(device.getName());
            binding.sensorX1.setText(String.valueOf(device.getDianjix1()));
            binding.sensorX2.setText(String.valueOf(device.getJixiex2()));
            binding.sensorX3.setText(String.valueOf(device.getFuhex3()));
            binding.sensorX4.setText(String.valueOf(device.getFeifuhex4()));
            binding.frequency.setText(String.valueOf(device.getFrequency()));
            binding.bearingBushArg1.setText(String.valueOf(device.getZhouxiangzongjianxi()));
            binding.bearingBushArg2.setText(String.valueOf(device.getFuhea()));
            binding.bearingBushArg3.setText(String.valueOf(device.getFeifuheb()));
            binding.bearingBushArg4.setText(String.valueOf(device.getFuhebaojing()));
            binding.bearingBushArg5.setText(String.valueOf(device.getFeifuhebaojing()));
            binding.bearingBushArg6.setText(String.valueOf(device.getFuheyujing()));
            binding.bearingBushArg7.setText(String.valueOf(device.getFeifuheyujing()));
            binding.bearingBushArg8.setText(String.valueOf(device.getFuhecezhoubaojing()));
            binding.bearingBushArg9.setText(String.valueOf(device.getFeifuhecezhoubaojing()));
        }
    }
}
