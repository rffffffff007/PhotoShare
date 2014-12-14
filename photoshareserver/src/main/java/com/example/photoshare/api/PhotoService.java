package com.example.photoshare.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;
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
            Feed feed = new Feed();
            feed.setFeed_id("test1");
            feed.setUser_name("robot");
            feed.setFeed_desc("This is for test");
            feed.setPhoto_url("http://211.155.92.122:8080/images/test.jpg");
            feeds.add(feed);
        }
        
        @Override
        public String hello(String name) throws AException, TException {
            return "Hello " + name;
        }

        @Override
        public FeedList getFeedList(int page_num, int page_count)
                throws AException, TException {
            if (page_count <= 0) {
                throw new AException("page_count should be bigger than 0");
            }
            FeedList feedList = new FeedList();
            feedList.setPage_num(page_num);
            feedList.setPage_count(page_count);
            int totalPageNum = (feeds.size() + page_count - 1) / page_count;
            feedList.setTotal_page_num(totalPageNum);
            if (page_num > totalPageNum) {
                throw new AException("There are only " + totalPageNum + " pages.");
            }
            int endPos = feeds.size() - page_num * page_count;
            int startPos = endPos - page_count;
            startPos = startPos < 0 ? 0 : startPos;
            feedList.setFeeds(feeds.subList(startPos, endPos));
            return feedList;
        }

        @Override
        public Feed uploadFeed(Feed feed) throws AException, TException {
            feed.setFeed_id("" + System.currentTimeMillis());
            feeds.add(feed);
            return feed;
        }

    }
}
