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
public class CacheHelper {
    private static ImageLoader.ImageCache mImageMemoryCache;
    private static Cache mContentsDiskCache;
    private static Cache mImageRequestsDiskCache;

    public static void initialize(Context appContext) {
        Utils.ensureOnMainThread();

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

        mContentsDiskCache = new DiskBasedCache(
                getCacheDir(appContext, "contents"), 10 * 1024 * 1024); // 10MB cap.
        mContentsDiskCache.initialize();

        mImageRequestsDiskCache = new DiskBasedCache(
                getCacheDir(appContext, "image_requests"), 10 * 1024 * 1024); // 10MB cap.
    }

    public static Cache getImageRequestsDiskCache() {
        return mImageRequestsDiskCache;
    }

    public static ImageLoader.ImageCache getImageMemoryCache() {
        return mImageMemoryCache;
    }

    public static Cache getContentsDiskCache() {
        return mContentsDiskCache;
    }

    private static File getCacheDir(Context appContext, String suffix) {
        File dir = new File(appContext.getCacheDir(), suffix);
        dir.mkdirs();
        return dir;
    }
}
