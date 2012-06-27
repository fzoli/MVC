package org.dyndns.fzoli.http.desktop;

import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.dyndns.fzoli.http.HttpClientWrapper;
import org.dyndns.fzoli.http.HttpExecutor;
import org.dyndns.fzoli.http.HttpUrl;

/**
 *
 * @author zoli
 */
public class DefaultHttpExecutor extends HttpExecutor {

    public DefaultHttpExecutor() {
    }

    public DefaultHttpExecutor(HttpUrl url) {
        super(url);
    }

    public DefaultHttpExecutor(HttpUrl url, String encode) {
        super(url, encode);
    }

    public DefaultHttpExecutor(HttpUrl url, String usr, String passwd) {
        super(url, usr, passwd);
    }

    public DefaultHttpExecutor(HttpUrl url, String encode, String usr, String passwd) {
        super(url, encode, usr, passwd);
    }
    
    @Override
    protected AbstractHttpClient createThreadSafeClient() {
        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
        return new DefaultHttpClient(cm);
    }

    @Override
    protected HttpClientWrapper createHttpClientWrapper() {
        return new DefaultHttpClientWrapper(HTTP_CLIENT);
    }
    
}