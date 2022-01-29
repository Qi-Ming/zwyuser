package com.baowu.zwyuser;

import android.app.Application;

public class MyApplication extends Application {
    public static final String USER_ID = "USER_ID";
    public static final String USER_SP = "USER_SP";
    public static String NEW_ALARM = "NEW_ALARM";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
