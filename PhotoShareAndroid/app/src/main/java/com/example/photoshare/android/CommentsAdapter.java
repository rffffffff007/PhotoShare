package com.example.photoshare.android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.photoshare.android.net.RPCHelper;
import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Comment;
import com.example.photoshare.thrift.CommentList;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;

import org.apache.thrift.TException;

import java.util.ArrayList;

import static junit.framework.Assert.assertNotNull;


/**
 * Created by faylon on 12/17/14.
 */
public class CommentsAdapter extends BaseAdapter {
    private Context mContext;
    private CommentList mCommentList;


    public CommentsAdapter(Context context) {
        mContext = context;
        //mCommentList = new CommentList();
        //mCommentList.setComments(new ArrayList<Comment>());
    }



    @Override
    public int getCount() {
        return mCommentList.getCommentsSize();
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_comment, parent, false);
        }
        // TODO set name and content
        TextView commentUName = (TextView)convertView.findViewById(R.id.comment_uname);
        commentUName.setText("Lao He:");
        TextView commentContent = (TextView)convertView.findViewById(R.id.comment_content);
        commentContent.setText("Lao He's comment. -----------");
        return convertView;
    }

    public void addComment(Comment comment) {
        assertNotNull(mCommentList);
        assertNotNull(comment);
        if (mCommentList.comments == null) {
            mCommentList.setComments(new ArrayList<Comment>());
        }
        mCommentList.comments.add(comment);
    }

    public void setCommentList(CommentList commentList) {
        assertNotNull(commentList);
        mCommentList = commentList;
    }
}
