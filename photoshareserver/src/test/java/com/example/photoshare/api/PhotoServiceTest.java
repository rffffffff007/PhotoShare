package com.example.photoshare.api;

import junit.framework.TestCase;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

import com.example.photoshare.thrift.AException;
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
    
    public void testGetFeedList() throws AException, TException {
        FeedList feedList = mPhotoClient.getFeedList(null, 10);
        for (Feed feed : feedList.getFeeds()) {
            System.out.println(feed.toString());
        }
        assertTrue(feedList.getFeedsSize() > 0);
    }
    
    public void testUploadFeed() throws AException, TException {
        FeedUploadReq feed = new FeedUploadReq();
        feed.user_name = "testcase";
        feed.feed_desc = "unit test " + System.currentTimeMillis();
        String text = "bitmap " + System.currentTimeMillis();
        feed.setPhoto_data(text.getBytes());
        Feed resFeed = mPhotoClient.uploadFeed(feed);
        assertTrue(resFeed.isSetFeed_id());
    }
}
