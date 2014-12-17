package com.example.photoshare.android;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Comment;
import com.example.photoshare.thrift.CommentList;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TException;

import static junit.framework.Assert.assertNotNull;

public class FeedViewActivity extends ActionBarActivity  implements
        View.OnClickListener {
    public static final String EXTRA_FEED = "extra_feed";
    private TextView mDesc;
    private NetworkImageView mImage;
    private ViewGroup mCommentsContainer;
    private CommentsAdapter mCommentsAdapter;
    private View mSubmitCommentBtn;
    private Feed mFeed;
    private CommentList mCommentList;

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
        mCommentsContainer = (ViewGroup) findViewById(R.id.comments_container);
        mSubmitCommentBtn = findViewById(R.id.comment_submit);
        mSubmitCommentBtn.setOnClickListener(this);
    }

    private void initContent() {
        mFeed = (Feed) getIntent().getSerializableExtra(EXTRA_FEED);
        if (mFeed != null) {
            mImage.setImageUrl(mFeed.getPhoto_url(), ImageLoaderHelper.getImageLoader());
            if (mFeed.isSetFeed_desc()) {
                mDesc.setText(mFeed.getFeed_desc());
            }
        }

        mCommentsAdapter = new CommentsAdapter(this);
        new GetCommentListTask(this).execute();
    }

    class GetCommentListTask extends BaseTask {
        private Context mContext;
        public GetCommentListTask(Context context) {
            super(context);
            mContext = context;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                return RPCHelper.getPhotoService().getCommentList(mFeed.getFeed_id());
            } catch (AException ae) {
                return ae;
            } catch (TException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o instanceof CommentList) {
                mCommentList = (CommentList)o;
                assertNotNull(mCommentList);
                mCommentsAdapter.setCommentList(mCommentList);
                for (int i = 0; i < mCommentsAdapter.getCount(); i++) {
                    View child = mCommentsAdapter.getView(i, null, null);
                    mCommentsContainer.addView(child, 0);
                }
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

    @Override
    public void onClick(View v) {
        if (v == mSubmitCommentBtn) {
            // Submit the comment.
            Comment comment = new Comment();
            comment.setContent("Test Content after submit.");
            comment.setSender_user_name("Lao he test submit name.");
            comment.setFeed_id(mFeed.getFeed_id());
            new SubmitCommentTask(this, comment).execute();
        }
    }

    class SubmitCommentTask extends BaseTask {
        private Context mContext;
        private Comment mComment;
        public SubmitCommentTask(Context context, Comment comment) {
            super(context);
            mContext = context;
            mComment = comment;
        }

        @Override
        protected Object doInBackground(Void... params) {
            try {
                return RPCHelper.getPhotoService().sendComment(mComment);
            } catch (AException ae) {
                return ae;
            } catch (TException e) {
                return e;
            }
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if (o instanceof Comment) {
                mCommentsAdapter.addComment((Comment)o);
                mCommentsAdapter.notifyDataSetChanged();
            }
        }
    }
}
