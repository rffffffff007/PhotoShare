package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import java.util.ArrayList;

/**
 * Created by zhouxiaobo on 12/16/14.
 */
public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final ImageLoader mImageLoader;
    private final LayoutInflater mLayoutInflater;
    private FeedList mFeeds;

    public ImageAdapter(Context c, ImageLoader imageLoader) {
        mContext = c;
        mLayoutInflater = LayoutInflater.from(mContext);
        mImageLoader = imageLoader;
        mFeeds = CacheHelper.getFeedsFromCache(c);
        if (mFeeds.getFeeds() == null) {
            mFeeds.setFeeds(new ArrayList<Feed>());
        }
    }

    @Override
    public int getCount() {
        return mFeeds.getFeeds().size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= getCount()) {
            return null;
        }
        return mFeeds.getFeeds().get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkImageView imageView;
        if (convertView == null) { // create a new view if no recycled view is available
            convertView = mLayoutInflater.inflate(
                    R.layout.grid_item_feed, parent, false /* attachToRoot */);
            imageView = (NetworkImageView) convertView.findViewById(R.id.image);
        } else {
            imageView = (NetworkImageView) convertView.findViewById(R.id.image);
            imageView.setImageUrl(null, mImageLoader);
        }
        String imageUrl = mFeeds.feeds.get(position).getPhoto_url();
        imageView.setImageUrl(imageUrl, mImageLoader);
        return convertView;
    }

    public void addFeed(Feed feed) {
        mFeeds.feeds.add(feed);
        CacheHelper.PutFeedsToCache(mFeeds, mContext);
    }
}
