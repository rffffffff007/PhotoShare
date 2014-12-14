package com.example.photoshare.api;

import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServlet;

public class BaseServlet extends TServlet {

    public BaseServlet(TProcessor processor) {
	    super(processor, new TBinaryProtocol.Factory());
    }

}
