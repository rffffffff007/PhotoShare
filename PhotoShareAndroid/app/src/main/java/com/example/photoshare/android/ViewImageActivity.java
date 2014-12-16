package com.example.photoshare.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

/**
 * Created by zhouxiaobo on 12/16/14.
 */
public class ViewImageActivity extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);
        Intent intent = getIntent();
        if (intent != null) {
            String imageUrl = intent.getStringExtra("image_url");
            if (imageUrl != null) {
                NetworkImageView imageView = (NetworkImageView) findViewById(R.id.image_view);
                imageView.setImageUrl(imageUrl, VolleyUtils.getImageLoader(this));
            }

            String imageDescription = intent.getStringExtra("image_description");
            if (imageDescription != null) {
                TextView textView = (TextView) findViewById(R.id.text_image_description);
                textView.setText(imageDescription);
            }
        }
    }
}
