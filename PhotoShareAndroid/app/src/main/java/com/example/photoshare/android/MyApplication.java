package com.example.photoshare.android;

import android.app.Application;
import android.content.Context;

/**
 * Created by zhouxiaobo on 12/17/14.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Context appContext = getApplicationContext();
        CacheHelper.initialize(appContext);
        ImageLoaderHelper.initialize();
        FeedListHelper.initialize();
    }
}
