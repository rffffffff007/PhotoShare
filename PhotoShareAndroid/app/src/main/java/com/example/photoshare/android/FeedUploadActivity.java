package com.example.photoshare.android;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedUploadReq;

import org.apache.thrift.TException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class FeedUploadActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Upload Feed");
        setContentView(R.layout.activity_feed_upload);
        Uri imageUri = getIntent().getParcelableExtra("extra_image_uri");
        Log.e("INFO", imageUri.toString());
        thisActivity = this;
        imageView  = (ImageView) findViewById(R.id.image);
        new DownSampleImageAsyncTask().execute(imageUri);
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
            new UploadImageAsyncTask().execute();
        }
        return super.onOptionsItemSelected(item);
    }

    class UploadImageAsyncTask extends AsyncTask<Void, Void, Feed> {
        @Override
        protected Feed doInBackground(Void... params) {
            Bitmap toUpload = image;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            toUpload.compress(Bitmap.CompressFormat.PNG, 100, out);
            ByteBuffer buffer = ByteBuffer.wrap(out.toByteArray());
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
        protected void onPostExecute(Feed result) {
            if (result != null) {
                Toast.makeText(thisActivity, "Your image has been uploaded!",
                        Toast.LENGTH_LONG).show();
                thisActivity.finish();
            } else {
                Toast.makeText(thisActivity, "Uploading failed!",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class DownSampleImageAsyncTask extends AsyncTask<Uri, Void, Bitmap> {
        private Uri uri;

        private final int imageWidth = 200; // getResources().getDimensionPixelOffset(R.dimen.meme_width);
        private final int imageHeight = 200; // getResources().getDimensionPixelOffset(R.dimen.meme_height);

        @Override
        protected Bitmap doInBackground(Uri... params) {

            uri = params[0];
            try {
                return decodeBitmapBounded(getInputStream(FeedUploadActivity.this, uri));
            } catch (IOException e) {
                Log.e("INFO", "Error reading bitmap", e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                image = result;
                imageView.setImageBitmap(result);
            }
        }

        private InputStream getInputStream(Context context, Uri uri) throws IOException {
            if (uri.getScheme().contentEquals(ContentResolver.SCHEME_CONTENT)) {
                return context.getContentResolver().openInputStream(uri);
            } else {
                return (InputStream) new URL(uri.toString()).getContent();
            }
        }

        private Bitmap decodeBitmapBounded(InputStream is)
                throws IOException {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(is, STREAM_BUFFER_SIZE);
            try {
                bufferedInputStream.mark(STREAM_BUFFER_SIZE);
                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                bmOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(bufferedInputStream, null, bmOptions);
                bufferedInputStream.reset();

                bmOptions.inJustDecodeBounds = false;
                bmOptions.inSampleSize = calculateInSampleSize(bmOptions.outWidth, bmOptions.outHeight,
                        imageWidth, imageHeight);
                return BitmapFactory.decodeStream(bufferedInputStream, null, bmOptions);
            } finally {
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
