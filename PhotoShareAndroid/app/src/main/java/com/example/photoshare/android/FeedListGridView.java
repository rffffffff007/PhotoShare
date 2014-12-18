package com.example.photoshare.android;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.GridView;

/**
 * Created by zhouxiaobo on 12/18/14.
 */
public class FeedListGridView extends GridView {
    private static int mMaxYOverScrollDistance = 200;

    Context mContext;

    public FeedListGridView(Context context) {
        super(context);
        mContext = context;
    }

    public FeedListGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected boolean overScrollBy(int deltaX, int deltaY, int scrollX,
                                   int scrollY, int scrollRangeX, int scrollRangeY,
                                   int maxOverScrollX, int maxOverScrollY, boolean isTouchEvent) {
        return super.overScrollBy(deltaX, deltaY, scrollX, scrollY,
                scrollRangeX, scrollRangeY, maxOverScrollX,
                mMaxYOverScrollDistance, isTouchEvent);
    }

    boolean mReadyToUpdate = true;

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (mReadyToUpdate) {
            if (scrollY == mMaxYOverScrollDistance) {
                new LoadMoreFeedsTask(
                        mContext, (android.widget.BaseAdapter) getAdapter()).execute();
                mReadyToUpdate = false;
            } else if (scrollY == -mMaxYOverScrollDistance) {
                new RefreshFeedsTask(
                        mContext, (android.widget.BaseAdapter) getAdapter()).execute();
                mReadyToUpdate = false;
            }
        } else if (-mMaxYOverScrollDistance / 2 < scrollY &&
                scrollY < mMaxYOverScrollDistance / 2) {
            mReadyToUpdate = true;
        }
    }
}
