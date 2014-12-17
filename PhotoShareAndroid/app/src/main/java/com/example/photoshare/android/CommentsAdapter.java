package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


/**
 * Created by faylon on 12/17/14.
 */
public class CommentsAdapter extends BaseAdapter {
    private Context mContext;

    public CommentsAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_comment, parent, false);
        }
        // TODO set name and content
        return convertView;
    }
}
