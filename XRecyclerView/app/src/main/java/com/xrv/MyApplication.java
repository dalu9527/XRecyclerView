package com.xrv;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
//import com.squareup.leakcanary.LeakCanary;

/**
 * @author xxoo
 * @date 5/12/2016.
 */
public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        initFresco();
//        initLeakCanary();
    }

    /**
     * 初始化LeakCanary
     */
//    private void initLeakCanary() {
//        LeakCanary.install(this);
//    }

    /**
     * 初始化Fresco
     */
    private void initFresco() {
        Fresco.initialize(this);
    }

}
