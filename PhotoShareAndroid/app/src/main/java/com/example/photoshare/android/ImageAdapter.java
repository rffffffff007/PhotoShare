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
            imageView = (NetworkImageView) mLayoutInflater.inflate(
                    R.layout.grid_view_item, parent, false /* attachToRoot */);
        } else {
            imageView = (NetworkImageView) convertView;
            imageView.setImageUrl(null, mImageLoader);
        }
        if (position >= mImageUrls.size()) { // imageUrls not yet downloaded!
            // TODO not good here.
            return imageView;
        }
        String imageUrl = mImageUrls.get(position);
        imageView.setImageUrl(imageUrl, mImageLoader);
        return imageView;
    }

    public void setImageUrls(List<String> imageUrls) {
        mImageUrls = imageUrls;
    }
}
