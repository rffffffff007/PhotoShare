package com.example.photoshare.android;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TException;

/**
* Created by zhouxiaobo on 12/18/14.
*/
public class LoadMoreFeedsTask extends BaseTask {
    private final static int FEED_COUNT_FOR_LOAD_MORE = 6;

    private Context mContext;
    private BaseAdapter mImageAdapter;

    public LoadMoreFeedsTask(Context context, BaseAdapter imageAdapter) {
        super(context);
        mContext = context;
        mImageAdapter = imageAdapter;
    }

    @Override
    protected Object doInBackground(Void... params) {
        try {
            Feed lastFeed = FeedListHelper.getLastFeed();
            String lastFeedId = lastFeed == null ? null : lastFeed.getFeed_id();
            return RPCHelper.getPhotoService().getFeedList(
                    lastFeedId, FEED_COUNT_FOR_LOAD_MORE);
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
            if (FeedListHelper.mergeFeedListFromEnd((FeedList) o)) {
                mImageAdapter.notifyDataSetChanged();
            } else {
                FeedListHelper.getFeedList().setHas_more_data(true);
                Toast.makeText(mContext, "No more photos.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
