package com.example.photoshare.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.content.Context;

import android.widget.Toast;

import com.example.photoshare.thrift.AException;

import org.apache.thrift.TException;

/**
 * Created by hezhijian on 12/17/14.
 */
abstract class BaseTask extends AsyncTask<Void, Void, Object> {
    private ProgressDialog mProgress;
    protected Context mContext;

    public BaseTask(Context context) {
        this(context, "Loading");
    }

    public BaseTask(Context context, String description) {
        mContext = context;
        mProgress = new ProgressDialog(context);
        mProgress.setTitle(description);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (!mProgress.isShowing()) {
            mProgress.show();
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (mProgress.isShowing()) {
            mProgress.dismiss();
        }
        if (o instanceof AException) {
            String msg = ((AException) o).getMessage();
            Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
        } else if (o instanceof TException) {
            Toast.makeText(mContext, "Cannot connect to internet.", Toast.LENGTH_SHORT).show();
        }
    }
}