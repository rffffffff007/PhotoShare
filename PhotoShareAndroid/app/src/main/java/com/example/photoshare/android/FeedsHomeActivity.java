package com.example.photoshare.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TException;

public class FeedsHomeActivity extends ActionBarActivity implements
        GridView.OnItemClickListener, View.OnClickListener {
    private static final int FEED_COUNT_FOR_EACH_REQUEST = 6;

    private static final String LOG_TAG = "FeedsHomeActivity";
    private GridView mGridView;
    private View mBtnMore;
    private ImageAdapter mImageAdapter;
    private Activity mHomeActivity;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHomeActivity = this;
        setTitle("Soap Fun");
        setContentView(R.layout.activity_feeds_home);
        initElements();
        initContent();
        if (Utils.GetUserName(this).isEmpty()) {
            inputUserName();
        } else {
            Log.d("INFO", "User name fetched: " + Utils.GetUserName(mHomeActivity));
        }
    }

    private void inputUserName() {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setCancelable(false);
        alert.setTitle("Please input your username:");
        alert.setMessage("e.g. Lao He");
        View inputView = LayoutInflater.from(mHomeActivity).inflate(
                R.layout.input_username, null, false /* attachToRoot */);
        alert.setView(inputView);
        final EditText input = (EditText) inputView.findViewById(R.id.input_username_edit);;

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getEditableText().toString();
                if (value.isEmpty()) {
                    Toast.makeText(
                            mHomeActivity, "User name cannot be empty", Toast.LENGTH_SHORT).show();
                    inputUserName();
                } else {
                    Toast.makeText(
                            mHomeActivity, "User name set: " + value, Toast.LENGTH_SHORT).show();
                    Utils.SetUserName(mHomeActivity, value);
                    Log.d("INFO", "User name: " + Utils.GetUserName(mHomeActivity));
                }
            }

        });

        alert.show();
    }

    private void initElements() {
        mGridView = (GridView) findViewById(R.id.grid_view);
        mBtnMore = findViewById(R.id.btn_more);

        mGridView.setOnItemClickListener(this);
        mBtnMore.setOnClickListener(this);
    }

    private void initContent() {
        mImageAdapter = new ImageAdapter(this, ImageLoaderHelper.getImageLoader());
        mGridView.setAdapter(mImageAdapter);

        if (FeedListHelper.getFeedList().getFeedsSize() == 0) {
            new MoreFeedsTask(this).execute();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(getBaseContext(), FeedViewActivity.class);
        Feed feed = (Feed) mImageAdapter.getItem(position);
        intent.putExtra(FeedViewActivity.EXTRA_FEED, feed);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        if (v == mBtnMore) {
            new MoreFeedsTask(this).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeds_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_pick) {
            dispatchPickingImageFromGalleryIntent();
            return true;
        } else if (id == R.id.action_take) {
            photoUri = dispatchImageCaptureIntent();
            return true;
        } else if (id == R.id.action_change_name) {
            // TODO open a dialog to random pick name.
            return true;
        } else if (id == R.id.action_refresh) {
            // TODO refresh the feedlist.
            new RefreshFeedsTask(this).execute();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Uri photoUri;

    private static final int IMAGE_CAPTURE_REQUEST_CODE = 1;
    private static final int IMAGE_PICKING_REQUEST_CODE = 2;

    private void dispatchPickingImageFromGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                IMAGE_PICKING_REQUEST_CODE);
    }

    private Uri dispatchImageCaptureIntent() {
        Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri resultUri = null;
        if (imageCaptureIntent.resolveActivity(getPackageManager()) != null) {
            resultUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new ContentValues());
            Log.d(LOG_TAG, "Original image will be saved to:\n" + resultUri);
            imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, resultUri);
            startActivityForResult(imageCaptureIntent, IMAGE_CAPTURE_REQUEST_CODE);
        } else {
            Toast.makeText(
                    this, "Camera not available", Toast.LENGTH_LONG).show();
        }
        return resultUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        if (requestCode == IMAGE_CAPTURE_REQUEST_CODE) {
            if (resultCode == RESULT_OK && photoUri != null) {
                Intent intent = new Intent(this, FeedUploadActivity.class);
                intent.putExtra("extra_image_uri", photoUri);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("INFO", "User cancelled taking picture.");
            } else {
                Toast.makeText(this, "Failed to start the camera.", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == IMAGE_PICKING_REQUEST_CODE) {
            if(resultCode == RESULT_OK && returnedIntent != null && returnedIntent.getData() != null) {
                Uri uri = returnedIntent.getData();
                Log.d("INFO", "Image picked: " + uri.toString());
                Intent intent = new Intent(this, FeedUploadActivity.class);
                intent.putExtra("extra_image_uri", uri);
                startActivity(intent);
            } else if (resultCode == RESULT_CANCELED) {
                Log.d("INFO", "User cancelled picture picking.");
            } else {
                Toast.makeText(this, "Error when trying to pick a picture from gallery.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    class RefreshFeedsTask extends BaseTask {
        public RefreshFeedsTask(Context context) {
            super(context);
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                return RPCHelper.getPhotoService().getFeedList(null, FEED_COUNT_FOR_EACH_REQUEST);
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
                    Toast.makeText(
                            FeedsHomeActivity.this, "No new photos.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    class MoreFeedsTask extends BaseTask {
        public MoreFeedsTask(Context context) {
            super(context);
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                Feed lastFeed = FeedListHelper.getLastFeed();
                String lastFeedId = lastFeed == null ? null : lastFeed.getFeed_id();
                return RPCHelper.getPhotoService().getFeedList(
                        lastFeedId, FEED_COUNT_FOR_EACH_REQUEST);
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
                    Toast.makeText(
                            FeedsHomeActivity.this, "No more photos.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
