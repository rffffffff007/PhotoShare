package com.example.photoshare.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Looper;

import java.util.Calendar;
import java.util.Date;

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
        return new java.text.SimpleDateFormat("yyyy/MMM/dd HH:mm").format(
                new java.util.Date (timestamp));
    }


    public static String GetShortReadableDate(long timestamp) {
        Date date = new Date(timestamp);
        Calendar cal = Calendar.getInstance();
        Calendar calNow = Calendar.getInstance();
        cal.setTime(date);
        calNow.setTime(new Date());
        boolean sameDay = cal.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == calNow.get(Calendar.DAY_OF_YEAR);
        if (sameDay)
            return new java.text.SimpleDateFormat("HH:mm").format(date);
        else
            return new java.text.SimpleDateFormat("MM/DD").format(date);
    }
}