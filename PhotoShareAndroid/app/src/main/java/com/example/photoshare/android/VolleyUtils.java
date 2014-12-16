package com.example.photoshare.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Looper;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by zhouxiaobo on 12/15/14.
 */
public final class VolleyUtils {
    private static class VolleyImageCache implements ImageLoader.ImageCache {
        private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(100);

        @Override
        public Bitmap getBitmap(String s) {
            return cache.get(s);
        }

        @Override
        public void putBitmap(String s, Bitmap bitmap) {
            cache.put(s, bitmap);
        }
    }

    private VolleyUtils() {
    }

    private static ImageLoader imageLoader;
    private static RequestQueue requestQueue;

    public static RequestQueue getRequestQueue(Context context) {
        ensureOnMainThread();
        if (requestQueue == null) {
            requestQueue = createRequestQueue(context);
            requestQueue.start();
        }
        return requestQueue;
    }

    public static void setRequestQueue(RequestQueue rq) {
        requestQueue = rq;
    }

    public static ImageLoader getImageLoader(Context context) {
        ensureOnMainThread();
        if (imageLoader == null) {
            imageLoader = new ImageLoader(getRequestQueue(context), new VolleyImageCache());
        }
        return imageLoader;
    }

    private static RequestQueue createRequestQueue(Context context) {
        // Ensure we have the application context - do not want to hold on to a shorter-lived
        // context (like an Activity).
        Context appContext = context.getApplicationContext();

        Network network = new BasicNetwork(new HurlStack());
        Cache cache = new DiskBasedCache(appContext.getCacheDir(), 10 * 1024 * 1024); // 10MB cap.
        return new RequestQueue(cache, network);
    }

    private static void ensureOnMainThread() {
        if (Looper.getMainLooper().getThread() != Looper.myLooper().getThread()) {
            throw new IllegalStateException("Must be called on the main thread.");
        }
    }
}
