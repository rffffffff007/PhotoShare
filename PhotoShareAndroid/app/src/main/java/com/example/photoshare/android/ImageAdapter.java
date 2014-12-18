package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by zhouxiaobo on 12/16/14.
 */
public class ImageAdapter extends BaseAdapter {
    private final ImageLoader mImageLoader;
    private final LayoutInflater mLayoutInflater;

    public ImageAdapter(Context context, ImageLoader imageLoader) {
        mLayoutInflater = LayoutInflater.from(context);
        mImageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return FeedListHelper.getFeedList().getFeedsSize();
    }

    @Override
    public Object getItem(int position) {
        if (position >= getCount()) {
            return null;
        }
        return FeedListHelper.getFeedList().getFeeds().get(position);
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
        String imageUrl = FeedListHelper.getFeedList().getFeeds().get(position).getPhoto_url();
        imageView.setImageUrl(imageUrl, mImageLoader);
        return convertView;
    }
}