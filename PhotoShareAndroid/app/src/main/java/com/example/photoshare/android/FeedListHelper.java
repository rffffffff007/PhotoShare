package com.example.photoshare.android;

import android.util.Log;

import com.android.volley.Cache;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import java.util.List;

/**
 * Created by zhouxiaobo on 12/17/14.
 */
public class FeedListHelper {
    private static FeedList mFeedList;
    private static final String FEED_LIST_CACHE_KEY = "FeedList";

    public static void initialize() {
        Utils.ensureOnMainThread();
        mFeedList = new FeedList();
        Cache cache = CacheHelper.getContentsDiskCache();
        Cache.Entry entry = cache.get(FEED_LIST_CACHE_KEY);
        if (entry == null) {
            return;
        }
        TDeserializer deserializer = new TDeserializer();
        try {
            deserializer.deserialize(mFeedList, entry.data);
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    public static FeedList getFeedList() {
        return mFeedList;
    }

    public static boolean mergeFeedListFromFront(FeedList feedList) {
        if (feedList.getFeedsSize() == 0) {
            return false;
        }
        Feed firstFeed = getFirstFeed();
        if (firstFeed == null) {
            mFeedList = feedList;
            putFeedsToCache();
            return true;
        }
        int i = 0;
        for (; i < feedList.getFeedsSize(); ++i) {
            if (feedList.getFeeds().get(i).getFeed_id().compareTo(firstFeed.getFeed_id()) <= 0) {
                break;
            }
        }
        if (i == 0) {
            return false;
        }
        List<Feed> newFeeds = feedList.getFeeds().subList(0, i);
        if (mFeedList.getFeeds() != null) {
            newFeeds.addAll(mFeedList.getFeeds());
        }
        mFeedList.setFeeds(newFeeds);
        putFeedsToCache();
        return true;
    }

    public static boolean mergeFeedListFromEnd(FeedList feedList) {
        if (feedList.getFeedsSize() == 0) {
            return false;
        }
        if (mFeedList.getFeeds() == null) {
            mFeedList.setFeeds(feedList.getFeeds());
        } else {
            mFeedList.getFeeds().addAll(feedList.getFeeds());
        }
        mFeedList.setHas_more_data(feedList.isHas_more_data());
        putFeedsToCache();
        return true;
    }

    public static Feed getLastFeed() {
        if (mFeedList.getFeedsSize() == 0) {
            return null;
        }
        return mFeedList.getFeeds().get(mFeedList.getFeedsSize() - 1);
    }

    private static Feed getFirstFeed() {
        if (mFeedList.getFeedsSize() == 0) {
            return null;
        }
        return mFeedList.getFeeds().get(0);
    }

    private static void putFeedsToCache() {
        Cache.Entry entry = new Cache.Entry();
        TSerializer serializer = new TSerializer();
        try {
            entry.data = serializer.serialize(mFeedList);
        } catch (TException e) {
            e.printStackTrace();
        }
        if (entry.data != null) {
            CacheHelper.getContentsDiskCache().put(FEED_LIST_CACHE_KEY, entry);
        }
    }
}
