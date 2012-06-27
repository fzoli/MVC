package org.dyndns.fzoli.http.android;

import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
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

    public DefaultHttpExecutor(HttpUrl url, String encode, String usr, String passwd) {
        super(url, encode, usr, passwd);
    }

    public DefaultHttpExecutor(HttpUrl url, String usr, String passwd) {
        super(url, usr, passwd);
    }

    public DefaultHttpExecutor(HttpUrl url, String encode) {
        super(url, encode);
    }

    @Override
    protected HttpClientWrapper createHttpClientWrapper() {
        return new DefaultHttpClientWrapper(HTTP_CLIENT);
    }

    @Override
    protected AbstractHttpClient createThreadSafeClient() {
        DefaultHttpClient tmp = new DefaultHttpClient();
        HttpParams params = tmp.getParams();
        SchemeRegistry registry = tmp.getConnectionManager().getSchemeRegistry();
        ThreadSafeClientConnManager manager = new ThreadSafeClientConnManager(params, registry);
        return new DefaultHttpClient(manager, params);
    }
    
}