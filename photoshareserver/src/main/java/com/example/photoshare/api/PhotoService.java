package com.example.photoshare.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.IPhotoService;

public class PhotoService extends BaseServlet {

    private static final long serialVersionUID = 1L;

    public PhotoService() {
        super(new IPhotoService.Processor<PhotoServiceImpl>(
                new PhotoServiceImpl()));
    }

    public static class PhotoServiceImpl implements IPhotoService.Iface {
        
        @Override
        public String hello(String name) throws AException, TException {
            return "Hello " + name;
        }

        @Override
        public List<Feed> getFeedList(int page, int page_count)
                throws AException, TException {
            List<Feed> feeds = new ArrayList<Feed>();
            Feed feed = new Feed();
            feed.setFeed_id("test1");
            feed.setUser_name("robot");
            feed.setFeed_desc("This is for test");
            feed.setPhoto_url("http://211.155.92.122:8080/images/test.jpg");
            feeds.add(feed);
            return feeds;
        }

        @Override
        public Feed uploadFeed(Feed feed) throws AException, TException {
            feed.setFeed_id("new generated id");
            return feed;
        }

    }
}
