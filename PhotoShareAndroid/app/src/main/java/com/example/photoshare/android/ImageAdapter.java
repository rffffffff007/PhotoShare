package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.photoshare.thrift.Feed;

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
        Feed feed = FeedListHelper.getFeedList().getFeeds().get(position);
        imageView.setImageUrl(feed.getPhoto_url(), mImageLoader);

        TextView nameView = (TextView) convertView.findViewById(R.id.name);
        if (feed.isSetUser_name()) {
            nameView.setText(feed.getUser_name());
        } else {
            nameView.setText("XXX");
        }
        TextView timeView = (TextView) convertView.findViewById(R.id.time);
        if (feed.isSetTimestamp()) {
            timeView.setText(Utils.GetReadableDate(feed.getTimestamp()));
        } else {
            timeView.setText("");
        }
        return convertView;
    }
}