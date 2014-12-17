package com.example.photoshare.android;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.photoshare.thrift.Feed;

public class FeedViewActivity extends ActionBarActivity {
    public static final String EXTRA_FEED = "extra_feed";
    private TextView mDesc;
    private NetworkImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Image by XXX");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_feed_view);

        initElements();
        initContent();
    }

    private void initElements() {
        mDesc = (TextView) findViewById(R.id.desc);
        mImage = (NetworkImageView) findViewById(R.id.image);
    }

    private void initContent() {
        Feed feed = (Feed) getIntent().getSerializableExtra(EXTRA_FEED);
        if (feed != null) {
            mImage.setImageUrl(feed.getPhoto_url(), VolleyUtils.getImageLoader(this));
            if (feed.isSetFeed_desc()) {
                mDesc.setText(feed.getFeed_desc());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feed_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
