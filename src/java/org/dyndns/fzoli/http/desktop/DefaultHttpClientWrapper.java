package org.dyndns.fzoli.http.desktop;

import java.net.URL;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.dyndns.fzoli.http.HttpClientWrapper;
import org.dyndns.fzoli.http.trustmanager.StrictReloadableX509TrustManager;

/**
 *
 * @author zoli
 */
class DefaultHttpClientWrapper extends HttpClientWrapper {

    public DefaultHttpClientWrapper(HttpClient httpClient) {
        super(httpClient);
    }
    
    @Override
    public void wrapHttpClient(boolean verifyHostname, URL cacertPath, String cacertPassword) throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManager[] trustmanagers;
        if (cacertPath == null) {
            trustmanagers = new TrustManager[]{tm};
        }
        else {
            trustmanagers = new TrustManager[] {new StrictReloadableX509TrustManager(cacertPath, cacertPassword)};
        }
        ctx.init(null, trustmanagers, null);
        wrapHttpsUrlConnection(ctx, verifyHostname);
        SSLSocketFactory ssf = new SSLSocketFactory(ctx);
        if (!verifyHostname) {
            ssf.setHostnameVerifier(hv);
        }
        ClientConnectionManager ccm = HTTP_CLIENT.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));
    }
    
}