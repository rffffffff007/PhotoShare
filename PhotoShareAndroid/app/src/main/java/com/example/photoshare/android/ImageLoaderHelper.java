package com.example.photoshare.android;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

public final class ImageLoaderHelper {
    private static ImageLoader mImageLoader;

    public static void initialize() {
        Utils.ensureOnMainThread();
        RequestQueue requestQueue = createRequestQueue();
        requestQueue.start();
        mImageLoader = new ImageLoader(requestQueue, CacheHelper.getImageMemoryCache());
    }
    public static ImageLoader getImageLoader() {
        return mImageLoader;
    }

    private static RequestQueue createRequestQueue() {
        Network network = new BasicNetwork(new HurlStack());
        Cache cache = CacheHelper.getImageRequestsDiskCache();
        return new RequestQueue(cache, network);
    }
}
