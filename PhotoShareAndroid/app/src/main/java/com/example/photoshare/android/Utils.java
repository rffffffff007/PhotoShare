package com.example.photoshare.android;

import android.app.Activity;
import android.content.SharedPreferences;
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

    public static String GetUserName(Activity activity) {
        SharedPreferences settings = activity.getSharedPreferences(
            "UserPrefs",
            Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        return settings.getString("username", "");
    }

    public static void SetUserName(Activity activity, String name) {
        SharedPreferences settings = activity.getSharedPreferences(
                "UserPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor prefEditor = settings.edit();
        prefEditor.putString("username", name);
        prefEditor.commit();
    }

    public static String GetReadableDate(long timestamp) {
        return new java.text.SimpleDateFormat("HH:mm:ss").format(
                new java.util.Date (timestamp));
    }
}