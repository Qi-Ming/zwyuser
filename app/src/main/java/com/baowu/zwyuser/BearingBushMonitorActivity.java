package com.baowu.zwyuser;

import static com.baowu.zwyuser.Utils.DEVICE_CONFIGURATION;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.baowu.zwyuser.bean.BearingBushDevice;
import com.baowu.zwyuser.databinding.ActivityBearingBushMonitorBinding;

import java.util.ArrayList;
import java.util.List;

public class BearingBushMonitorActivity extends AppCompatActivity {
    private ActivityBearingBushMonitorBinding binding;
    private List<Fragment> list;
    private BearingBushDevice device;
    private final String[] titles = {"监测1", "监测2", "监测3"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        binding.back.setOnClickListener(v -> onBackPressed());
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_bearing_bush_monitor);
        device = (BearingBushDevice) getIntent().getSerializableExtra(DEVICE_CONFIGURATION);
        list = new ArrayList<>();
        list.add(new BearingBushMonitor1());
        list.add(new BearingBushMonitor2());
        list.add(new BearingBushMonitor3());
        FragmentAdapter fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(), FragmentAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        binding.vp.setAdapter(fragmentAdapter);
        binding.tabLayout.setupWithViewPager(binding.vp);
    }

    public BearingBushDevice getDevice() {
        return device;
    }

    class FragmentAdapter extends FragmentPagerAdapter {

        public FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return list.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }
}

