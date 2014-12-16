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
    private final Context context;
    private final ImageLoader imageLoader;
    private final LayoutInflater layoutInflater;
    private List<String> imageUrls = new ArrayList<String>();

    public ImageAdapter(Context c, ImageLoader imageLoader) {
        context = c;
        layoutInflater = LayoutInflater.from(context);
        this.imageLoader = imageLoader;
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= imageUrls.size()) {
            return null;
        }
        return imageUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NetworkImageView imageView;
        if (convertView == null) { // create a new view if no recycled view is available
            imageView = (NetworkImageView) layoutInflater.inflate(
                    R.layout.grid_view_item, parent, false /* attachToRoot */);
        } else {
            imageView = (NetworkImageView) convertView;
            imageView.setImageUrl(null, imageLoader);
        }
        if (position >= imageUrls.size()) { // imageUrls not yet downloaded!
            return imageView;
        }
        String imageUrl = imageUrls.get(position);
        imageView.setImageUrl(imageUrl, imageLoader);
        return imageView;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
