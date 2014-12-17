package com.example.photoshare.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;
import com.example.photoshare.thrift.FeedUploadReq;
import com.example.photoshare.thrift.IPhotoService;

public class PhotoService extends BaseServlet {

    private static final long serialVersionUID = 1L;

    public PhotoService() {
        super(new IPhotoService.Processor<PhotoServiceImpl>(
                new PhotoServiceImpl()));
    }

    public static class PhotoServiceImpl implements IPhotoService.Iface {
        List<Feed> feeds = new ArrayList<Feed>();
        
        protected PhotoServiceImpl() {
            feeds = new ArrayList<Feed>();
            FeedUploadReq feed = new FeedUploadReq();
            feed.setUser_name("robot");
            feed.setFeed_desc("This is for test");
            try {
                uploadFeed(feed);
            } catch (TException e) {
                e.printStackTrace();
            }
        }
        
        @Override
        public String hello(String name) throws AException, TException {
            return "Hello " + name;
        }

        @Override
        public FeedList getFeedList(String last_feed_id, int page_count)
                throws AException, TException {
            if (page_count <= 0) {
                throw new AException("page_count should be bigger than 0");
            }
            FeedList feedList = new FeedList();
            int startPos = 0;
            if (last_feed_id != null) {
                for (int i = 0; i < feeds.size(); ++i) {
                    if (last_feed_id.compareTo(feeds.get(i).getFeed_id()) <= 0) {
                        startPos = i;
                        break;
                    }
                }
            }
            int endPos = startPos + page_count;
            startPos = startPos < 0 ? 0 : startPos;
            feedList.setFeeds(feeds.subList(startPos, endPos));
            return feedList;
        }

        @Override
        public Feed uploadFeed(FeedUploadReq feedReq) throws AException,
                TException {
            Feed feed = new Feed();
            feed.setFeed_desc(feedReq.getFeed_desc());
            feed.setUser_name(feedReq.getUser_name());
            long timestamp = System.currentTimeMillis();
            feed.setFeed_id("" + timestamp);
            feed.setTimestamp(timestamp);
            feeds.add(feed);
            return feed;
        }

    }
}
