package com.arcblock.sdk.demo;

import android.app.Application;

import com.arcblock.corekit.ABCoreKit;

public class DemoApplication extends Application {

    public static DemoApplication INSTANCE = null;

    public static DemoApplication getInstance() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        // init ArcBlock CoreKit
        ABCoreKit.getInstance().init(this, true);
    }
}
