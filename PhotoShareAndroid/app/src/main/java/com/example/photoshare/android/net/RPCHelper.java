package com.example.photoshare.android.net;


import com.example.photoshare.thrift.IPhotoService;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;

public class RPCHelper {
    private static final String TAG = "RPCHelper";
    public static final String HOST_URL = "http://211.155.92.122:8080/photoshare/";

    private static final int SOCKET_TIME_OUT = 25000;
    private static final int CONNECTION_TIME_OUT = 25000;

    private static HttpClient getHttpClient() {
        HttpParams param = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(param, CONNECTION_TIME_OUT);
        HttpConnectionParams.setSoTimeout(param, SOCKET_TIME_OUT);
        return new DefaultHttpClient(param);
    }

    private static TProtocol getProtocol(TTransport transport) {
        TBinaryProtocol protocol = new TBinaryProtocol(transport);
        return protocol;
    }

    private static TProtocol getTProtocol(String url) throws TException {
        HttpClient client = getHttpClient();
        TTransport transport = new THttpClient(HOST_URL + url, client);
        transport.open();
        return getProtocol(transport);
    }

    public static IPhotoService.Client getPhotoService() throws TException {
        return new IPhotoService.Client(getTProtocol("photoservice"));
    }

}
