package com.example.photoshare.android;

import android.os.Looper;

/**
 * Created by zhouxiaobo on 12/17/14.
 */
public class Utils {
    public static void ensureOnMainThread() {
        if (Looper.getMainLooper().getThread() != Looper.myLooper().getThread()) {
            throw new IllegalStateException("Must be called on the main thread.");
        }
    }
}