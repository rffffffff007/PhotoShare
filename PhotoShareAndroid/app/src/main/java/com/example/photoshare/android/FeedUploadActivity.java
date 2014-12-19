package com.example.photoshare.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedUploadReq;

import org.apache.thrift.TException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class FeedUploadActivity extends ActionBarActivity {
    private static final String LOG_TAG = "FeedUploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upload Feed");
        setContentView(R.layout.activity_feed_upload);
        Uri imageUri = getIntent().getParcelableExtra("extra_image_uri");
        Log.e(LOG_TAG, imageUri.toString());
        thisActivity = this;
        imageView = (ImageView) findViewById(R.id.image);
        new DownSampleImageAsyncTask(this, imageUri).execute();
        description = (EditText) findViewById(R.id.desc_edit);
    }

    ImageView imageView;
    Bitmap image;
    Activity thisActivity;
    EditText description;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_upload, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_upload) {
            new UploadImageAsyncTask(this).execute();
        }
        return super.onOptionsItemSelected(item);
    }

    class UploadImageAsyncTask extends BaseTask {
        public UploadImageAsyncTask(Context context) {
            super(context, "Uploading your feed...");
        }

        @Override
        protected Feed doInBackground(Void... params) {
            Bitmap toUpload = image;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            toUpload.compress(Bitmap.CompressFormat.JPEG, 60, out);
            byte[] imageData = out.toByteArray();
            Log.i(LOG_TAG, "Image Size:" + (imageData.length / 1024 )+ " kb");
            ByteBuffer buffer = ByteBuffer.wrap(imageData);
            FeedUploadReq req = new FeedUploadReq();
            req.setPhoto_data(buffer);
            req.setUser_name(Utils.GetUserName(thisActivity));
            req.setFeed_desc(description.getText().toString());
            try {
                return RPCHelper.getPhotoService().uploadFeed(req);
            } catch (TException e) {
                Log.e("INFO", "Error uploading image, description: " + req.getFeed_desc(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                Toast.makeText(thisActivity, "Your image has been uploaded!",
                        Toast.LENGTH_SHORT).show();
                thisActivity.finish();
            } else {
                Toast.makeText(thisActivity, "Uploading failed!",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    class DownSampleImageAsyncTask extends BaseTask {
        private Uri mUri;

        public DownSampleImageAsyncTask(Context context, Uri uri) {
            super(context, "Loading image...");
            mUri = uri;
        }

        private final int imageWidth = 400;
        private final int imageHeight = 400;

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                return decodeBitmapBounded(mUri);
            } catch (IOException e) {
                Log.e("INFO", "Error reading bitmap", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (result != null) {
                image = (Bitmap) result;
                imageView.setImageBitmap(image);
                Log.i(LOG_TAG, "Image:" + image.getWidth() + "," + image.getHeight());
            }
            super.onPostExecute(result);
        }

        private InputStream getInputStream(Context context, Uri uri) throws IOException {
            if (uri.getScheme().contentEquals(ContentResolver.SCHEME_CONTENT)) {
                return context.getContentResolver().openInputStream(uri);
            } else {
                return (InputStream) new URL(uri.toString()).getContent();
            }
        }

        private Bitmap decodeBitmapBounded(Uri uri)
                throws IOException {
            BufferedInputStream bufferedInputStream = null;
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            try {
                bufferedInputStream = new BufferedInputStream(getInputStream(mContext, uri), STREAM_BUFFER_SIZE);
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(bufferedInputStream, null, bmOptions);
            } finally {
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            }
            try {
                bufferedInputStream = new BufferedInputStream(getInputStream(mContext, uri), STREAM_BUFFER_SIZE);
                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = calculateInSampleSize(bmOptions.outWidth, bmOptions.outHeight,
                        imageWidth, imageHeight);
                return BitmapFactory.decodeStream(bufferedInputStream, null, bmOptions);
            } finally {
                if (bufferedInputStream != null)
                    bufferedInputStream.close();
            }
        }

        private int calculateInSampleSize(int srcWidth, int srcHeight,
                                          int reqWidth, int reqHeight) {
            if ((reqHeight > 0) && (reqWidth > 0) && (srcHeight > reqHeight) && (srcWidth > reqWidth)) {
                return Math.min(srcWidth / reqWidth, srcHeight / reqHeight);
            } else {
                return 1;
            }
        }

        private static final int STREAM_BUFFER_SIZE = 64 * 1024;
    }
}
