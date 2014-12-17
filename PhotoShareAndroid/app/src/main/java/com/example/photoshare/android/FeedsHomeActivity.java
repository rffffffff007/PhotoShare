package com.example.photoshare.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.example.photoshare.thrift.Feed;

public class FeedsHomeActivity extends ActionBarActivity implements
        GridView.OnItemClickListener, View.OnClickListener {

    private EditText mImageUrl;
    private GridView mGridView;
    private View mBtnAdd;
    private ImageAdapter mImageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Feeds Home");
        setContentView(R.layout.activity_feeds_home);
        initElements();
        initContent();
    }

    private void initElements() {
        mImageUrl = (EditText) findViewById(R.id.image_url);
        mGridView = (GridView) findViewById(R.id.grid_view);
        mBtnAdd = findViewById(R.id.btn_add);
        mGridView.setOnItemClickListener(this);
        mBtnAdd.setOnClickListener(this);
    }

    private void initContent() {
        mImageAdapter = new ImageAdapter(this, ImageLoaderHelper.getImageLoader(this));
        mGridView.setAdapter(mImageAdapter);
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
        if (v == mBtnAdd) {
            Feed feed = new Feed();
            feed.setPhoto_url(mImageUrl.getText().toString());
            mImageAdapter.addFeed(feed);
            mImageAdapter.notifyDataSetChanged();
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
            // TODO pick from gallery
            Intent intent = new Intent(this, FeedUploadActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_take) {
            // TODO take camera photo
            Intent intent = new Intent(this, FeedViewActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_change_name) {
            // TODO open a dialog to random pick name.
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
