package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouxiaobo on 12/16/14.
 */
public class ImageAdapter extends BaseAdapter {
    private final Context mContext;
    private final ImageLoader mImageLoader;
    private final LayoutInflater mLayoutInflater;
    private List<String> mImageUrls = new ArrayList<String>();

    public ImageAdapter(Context c, ImageLoader imageLoader) {
        mContext = c;
        mLayoutInflater = LayoutInflater.from(mContext);
        mImageLoader = imageLoader;
    }

    public List<String> getImageUrls() {
        return mImageUrls;
    }

    @Override
    public int getCount() {
        return mImageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= mImageUrls.size()) {
            return null;
        }
        return mImageUrls.get(position);
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
        String imageUrl = mImageUrls.get(position);
        imageView.setImageUrl(imageUrl, mImageLoader);
        return convertView;
    }

    public void setImageUrls(List<String> imageUrls) {
        mImageUrls = imageUrls;
    }
}
