package com.example.photoshare.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;

import com.example.photoshare.thrift.AException;
import com.example.photoshare.thrift.Feed;

public class PhotoService extends BaseServlet {
	public PhotoService() {
        super();
    }

    private static final long serialVersionUID = 1L;

	public static class PhotoServiceImpl implements com.example.photoshare.thrift.PhotoService.Iface {

        @Override
        public String hello(String name) throws AException, TException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Feed> getFeedList(int page, int page_count)
                throws AException, TException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Feed uploadFeed(Feed feed) throws AException, TException {
            // TODO Auto-generated method stub
            return null;
        }
	    
	    
	}
}
