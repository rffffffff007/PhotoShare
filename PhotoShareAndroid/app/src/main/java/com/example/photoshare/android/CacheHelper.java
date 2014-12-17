package com.example.photoshare.android;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Cache;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.ImageLoader;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.io.File;

/**
* Created by zhouxiaobo on 12/17/14.
*/
class CacheHelper {

    public static FeedList getFeedsFromCache(Context context) {
        Utils.ensureOnMainThread();
        FeedList feeds = new FeedList();
        Cache cache = getContentsDiskCache(context);
        Cache.Entry entry = cache.get("feeds");
        if (entry == null) {
            return feeds;
        }
        TDeserializer deserializer = new TDeserializer();
        try {
            deserializer.deserialize(feeds, entry.data);
        } catch (TException e) {
            e.printStackTrace();
        }
        return feeds;
    }

    public static void PutFeedsToCache(FeedList feeds, Context context) {
        Cache.Entry entry = new Cache.Entry();
        TSerializer serializer = new TSerializer();
        try {
            entry.data = serializer.serialize(feeds);
        } catch (TException e) {
            e.printStackTrace();
        }
        if (entry.data != null) {
            getContentsDiskCache(context).put("feeds", entry);
        }
    }

    public static Cache getImageRequestsDiskCache(Context context) {
        Utils.ensureOnMainThread();
        if (mImageRequestsDiskCache == null) {
            mImageRequestsDiskCache = new DiskBasedCache(
                    getCacheDir(context, "image_requests"), 10 * 1024 * 1024); // 10MB cap.
        }
        return mImageRequestsDiskCache;
    }

    public static ImageLoader.ImageCache getImageMemoryCache() {
        Utils.ensureOnMainThread();
        if (mImageMemoryCache == null) {
            mImageMemoryCache = new ImageLoader.ImageCache() {
                @Override
                public Bitmap getBitmap(String s) {
                    return cache.get(s);
                }

                @Override
                public void putBitmap(String s, Bitmap bitmap) {
                    cache.put(s, bitmap);
                }

                private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(100);
            };
        }
        return mImageMemoryCache;
    }

    private static Cache getContentsDiskCache(Context context) {
        Utils.ensureOnMainThread();
        if (mContentsDiskCache == null) {
            mContentsDiskCache = new DiskBasedCache(
                    getCacheDir(context, "contents"), 10 * 1024 * 1024); // 10MB cap.
            mContentsDiskCache.initialize();
        }
        return mContentsDiskCache;
    }

    private static ImageLoader.ImageCache mImageMemoryCache;
    private static Cache mContentsDiskCache;
    private static Cache mImageRequestsDiskCache;

    private static File getCacheDir(Context context, String suffix) {
        // Ensure we have the application context - do not want to hold on to a shorter-lived
        // context (like an Activity).
        Context appContext = context.getApplicationContext();
        File dir = new File(appContext.getCacheDir(), suffix);
        dir.mkdirs();
        return dir;
    }
}
