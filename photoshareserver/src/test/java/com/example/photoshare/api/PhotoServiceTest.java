package com.example.photoshare.api;

import junit.framework.TestCase;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Comment;
import com.example.photoshare.thrift.CommentList;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;
import com.example.photoshare.thrift.FeedUploadReq;
import com.example.photoshare.thrift.IPhotoService;

public class PhotoServiceTest extends TestCase {
    private IPhotoService.Client mPhotoClient;
    private TTransport mTransport;
    private static final String API_ADDRESS = "http://211.155.92.122:8080/photoshare/photoservice";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mTransport = new THttpClient(API_ADDRESS);
        mTransport.open();
        TProtocol protocol = new TBinaryProtocol(mTransport);
        mPhotoClient = new IPhotoService.Client(protocol);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mTransport.close();
    }

    public void testHello() throws AException, TException {
        String val = mPhotoClient.hello("faylon");
        assertEquals(val, "Hello faylon");
    }

    public void testUploadAndGetFeed() throws Exception {
        // upload feed
        System.out.println("Upload Feed");
        FeedUploadReq feed = new FeedUploadReq();
        feed.user_name = "testcase";
        feed.feed_desc = "unit test " + System.currentTimeMillis();
        String text = "bitmap " + System.currentTimeMillis();
        feed.setPhoto_data(text.getBytes());
        Feed resFeed = mPhotoClient.uploadFeed(feed);
        System.out.println(resFeed.toString());
        assertTrue(resFeed.isSetFeed_id());

        // get feed
        FeedList feedList = mPhotoClient.getFeedList(null, 3);
        System.out.println("Get Feed List");
        for (Feed f : feedList.getFeeds()) {
            System.out.println(f.toString());
        }
        assertTrue(feedList.getFeedsSize() > 0);
        Feed firstFeed = feedList.getFeeds().get(0);
        assertTrue(firstFeed.getFeed_id().equals(resFeed.getFeed_id()));
        assertTrue(firstFeed.getFeed_desc().equals(feed.getFeed_desc()));
    }

    public void testSendAndGetComment() throws Exception {
        // send comment
        System.out.println("Send comment");
        String feed_id = "test_feed_id";
        Comment comment = new Comment();
        comment.setContent("A test comment body");
        comment.setFeed_id(feed_id);
        comment.setSender_user_name("test case");
        Comment resComment = mPhotoClient.sendComment(comment);
        System.out.println(resComment.toString());
        assertTrue(resComment.getFeed_id().equals(feed_id));
        
        // get comments
        System.out.println("Get comments");
        CommentList commentList = mPhotoClient.getCommentList(feed_id, 5);
        for (Comment c : commentList.getComments()) {
            System.out.println(c.toString());
        }
        assertTrue(commentList.getCommentsSize() > 0);
        Comment firstComment = commentList.getComments().get(0);
        assertTrue(firstComment.getFeed_id().equals(feed_id));
        assertTrue(firstComment.getContent().equals(comment.getContent()));
    }
}
