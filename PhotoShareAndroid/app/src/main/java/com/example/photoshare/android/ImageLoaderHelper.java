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

    public static ImageLoader getImageLoader(Context context) {
        Utils.ensureOnMainThread();
        if (mImageLoader == null) {
            RequestQueue requestQueue = createRequestQueue(context);
            requestQueue.start();
            mImageLoader = new ImageLoader(requestQueue, CacheHelper.getImageMemoryCache());
        }
        return mImageLoader;
    }

    private static RequestQueue createRequestQueue(Context context) {
        Network network = new BasicNetwork(new HurlStack());
        Cache cache = CacheHelper.getImageRequestsDiskCache(context);
        return new RequestQueue(cache, network);
    }
}
