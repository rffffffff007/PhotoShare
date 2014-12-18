package com.example.photoshare.android;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TException;

/**
* Created by zhouxiaobo on 12/18/14.
*/
public class RefreshFeedsTask extends BaseTask {
    private final static int FEED_COUNT_FOR_REFRESH = 6;

    Context mContext;
    BaseAdapter mImageAdapter;

    public RefreshFeedsTask(Context context, BaseAdapter imageAdapter) {
        super(context);
        mContext = context;
        mImageAdapter = imageAdapter;
    }

    @Override
    protected Object doInBackground(Void... params) {
        try {
            return RPCHelper.getPhotoService().getFeedList(null, FEED_COUNT_FOR_REFRESH);
        } catch (AException ae) {
            return ae;
        } catch (TException e) {
            return e;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (o instanceof FeedList) {
            if (FeedListHelper.mergeFeedListFromFront((FeedList) o)) {
                mImageAdapter.notifyDataSetChanged();
            } else {
                Toast.makeText(mContext, "No new photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
