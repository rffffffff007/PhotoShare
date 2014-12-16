package com.example.photoshare.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by faylon on 12/16/14.
 */
public class FeedsHomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Feeds Home");
        setContentView(R.layout.activity_feeds_home);
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
