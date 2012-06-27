package org.dyndns.fzoli.http.android;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;
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
public class DefaultHttpClientWrapper extends HttpClientWrapper {

    public DefaultHttpClientWrapper(HttpClient httpClient) {
        super(httpClient);
    }

    @Override
    public void wrapHttpClient(boolean verifyHostname, URL cacertPath, String cacertPassword) throws Exception {
    	KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManager[] trustmanagers;
        SSLSocketFactory ssf;
        if (cacertPath == null) {
            trustmanagers = new TrustManager[]{tm};
            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] {tm}, null);
            ssf = new SSLSocketFactory(keyStore) {
            	
            	@Override
            	public Socket createSocket() throws IOException {
            		return sslContext.getSocketFactory().createSocket();
            	}
            	
            	@Override
            	public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
            		return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);	
            	}
            	
            };
        }
        else {
            char[] passwd = cacertPassword == null ? null : cacertPassword.toCharArray();
            keyStore.load(cacertPath.openStream(), passwd);
            ssf = new SSLSocketFactory(keyStore, cacertPassword);
            trustmanagers = new TrustManager[] {new StrictReloadableX509TrustManager(cacertPath, cacertPassword)};
        }
        ctx.init(null, trustmanagers, null);
        wrapHttpsUrlConnection(ctx, verifyHostname);
        if (!verifyHostname) {
            ssf.setHostnameVerifier(hv);
        }
        ClientConnectionManager ccm = HTTP_CLIENT.getConnectionManager();
        SchemeRegistry sr = ccm.getSchemeRegistry();
        sr.register(new Scheme("https", ssf, 443));
    }
    
}