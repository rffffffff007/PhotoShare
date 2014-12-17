package com.example.photoshare.api;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Comment;
import com.example.photoshare.thrift.CommentList;
import com.example.photoshare.thrift.CommentsData;
import com.example.photoshare.thrift.Feed;
import com.example.photoshare.thrift.FeedList;
import com.example.photoshare.thrift.FeedUploadReq;
import com.example.photoshare.thrift.FeedsData;
import com.example.photoshare.thrift.IPhotoService;

public class PhotoService extends BaseServlet {

    private static final long serialVersionUID = 1L;

    private static final String IMAGE_URL = "http://211.155.92.122/images/";
    private static final String IMAGE_BASE = "/data/images/";
    private static final String DATA_BASE = "/data/";

    public PhotoService() {
        super(new IPhotoService.Processor<PhotoServiceImpl>(
                new PhotoServiceImpl()));
    }

    private static File writeFile(byte[] data, String filePath)
            throws IOException {
        File file = new File(filePath);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
        } catch (IOException e) {
            throw e;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
            }
        }
        return file;
    }

    private static void readFile(String filePath, OutputStream os)
            throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(filePath));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bis.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static void saveData(String key, TBase data) {
        try {
            TSerializer ts = new TSerializer();
            byte[] bytes = ts.serialize(data);
            writeFile(bytes, DATA_BASE + key);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void restoreData(String key, TBase data) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            readFile(DATA_BASE + key, bos);
            TDeserializer td = new TDeserializer();
            td.deserialize(data, bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class PhotoServiceImpl implements IPhotoService.Iface {
        private FeedsData feedsData;
        private CommentsData commentsData;

        private static final String DATA_KEY_FEEDS = "data_key_feeds";
        private static final String DATA_KEY_COMMENTS = "data_key_comments";

        private long lastSaveTime = 0;

        protected PhotoServiceImpl() {
            feedsData = new FeedsData(new ArrayList<Feed>());
            commentsData = new CommentsData(
                    new HashMap<String, List<Comment>>());
            restoreData(DATA_KEY_FEEDS, feedsData);
            restoreData(DATA_KEY_COMMENTS, commentsData);
        }

        @Override
        public String hello(String name) throws AException, TException {
            lastSaveTime = 0;
            saveDataIfNeed();
            return "Hello " + name;
        }

        private synchronized void saveDataIfNeed() {
            if (System.currentTimeMillis() - lastSaveTime > 1000 * 60 * 5) {
                saveData(DATA_KEY_FEEDS, feedsData);
                saveData(DATA_KEY_COMMENTS, commentsData);
                lastSaveTime = System.currentTimeMillis();
            }
        }

        @Override
        public FeedList getFeedList(String last_feed_id, int page_count)
                throws AException, TException {
            saveDataIfNeed();
            if (page_count <= 0) {
                throw new AException("page_count should be bigger than 0");
            }
            FeedList feedList = new FeedList();
            int feedsSize = feedsData.getFeedSize();
	    int endPos = feedsSize - 1;
            if (last_feed_id != null) {
                for (endPos = feedsSize - 1; endPos >= 0; --endPos) {
                    if (last_feed_id.compareTo(feedsData.getFeed().get(endPos)
                            .getFeed_id()) > 0) {
                        System.out.println(last_feed_id + "," + endPos + "," + feedsData.getFeed().get(endPos).getFeed_id());
                        break;
                    }
                }
            }
            int startPos = endPos + 1 - page_count;
            startPos = startPos >= 0 ? startPos : 0;
            System.out.println(last_feed_id + "," + startPos + "," + endPos);
            for (int i = endPos; i >= startPos; i--) {
                feedList.addToFeeds(feedsData.getFeed().get(i));
            }
            return feedList;
        }

        private synchronized void insertFeed(Feed feed) {
            feedsData.getFeed().add(feed);
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
            insertFeed(feed);

            if (feedReq.isSetPhoto_data()) {
                try {
                    String filepath = IMAGE_BASE + System.currentTimeMillis()
                            + ".jpg";
                    File file = writeFile(feedReq.getPhoto_data(), filepath);
                    feed.setPhoto_url(IMAGE_URL + file.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new AException("Error in writing image file.");
                }
            }
            return feed;
        }

        private synchronized void insertComment(Comment comment) {
            String feed_id = comment.getFeed_id();
            Map<String, List<Comment>> commentMap = commentsData
                    .getFeed_comments();
            if (!commentMap.containsKey(feed_id)) {
                commentMap.put(feed_id, new ArrayList<Comment>());
            }
            commentMap.get(comment.feed_id).add(comment);
        }

        @Override
        public Comment sendComment(Comment comment) throws AException,
                TException {
            insertComment(comment);
            comment.setTimestamp(System.currentTimeMillis());
            return comment;
        }

        @Override
        public CommentList getCommentList(String feed_id, int comments_count)
                throws AException, TException {
            Map<String, List<Comment>> commentMap = commentsData
                    .getFeed_comments();
            List<Comment> comments = commentMap.get(feed_id);

            CommentList commentList = new CommentList(new ArrayList<Comment>());
            for (int i = comments.size() - 1; i >= 0 && i >= comments.size() - comments_count; i--) {
                commentList.addToComments(comments.get(i));
            }
            return commentList;
        }

    }
}
