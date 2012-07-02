package org.dyndns.fzoli.http;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.dyndns.fzoli.http.data.FormEntityReturn;
import org.dyndns.fzoli.http.data.HttpResponseReturn;
import org.dyndns.fzoli.http.data.HttpStreamReturn;

/**
 * HttpExecutor Map kulcsa.
 * @author Farkas Zoltán
 */
class ExecuteData {
    
    private HttpUrl url;
    private Map<String, List<String>> map;

    public ExecuteData(HttpUrl url, Map<String, List<String>> map) {
        this.url = url;
        this.map = map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ExecuteData)) return false;
        ExecuteData ed = (ExecuteData) obj;
        if (!url.equals(ed.url)) return false;
        if ((map == null && ed.map != null) || (map != null && ed.map == null)) return false;
        if (map != null && !map.equals(ed.map)) return false;
        return true;
    }
    
}

/**
 * HttpClient alapú, HTTP kapcsolatot használó osztály.
 * - Szervernek POST alapú üzenetet küld.
 * - A szerver válaszát visszaadja InputStream vagy String objektumban valamint a HTTP státuszkódot.
 * - Képes egyszerre több kapcsolatot is kezelni.
 * - Ha a szerver igényli, beállítható egy felhasználónév és jelszó is.
 * - Ha a kapcsolat titkosított és a tanusítvány nem megbízható, beállítható, hogy másképp kezelje.
 */
public abstract class HttpExecutor {
    
    private Locale locale;
    private HttpUrl url;
    private String encode;
    private int connectionTimeout = 5000;
    private UsernamePasswordCredentials creds = null;
    
    protected final AbstractHttpClient HTTP_CLIENT;
    
    private final Map<ExecuteData, HttpPost> EXECUTE_MAP = new HashMap<ExecuteData, HttpPost>();
    
    public HttpExecutor() {
        this(null);
    }
    
    public HttpExecutor(HttpUrl url) {
        this(url, null, null);
    }
    
    public HttpExecutor(HttpUrl url, String encode) {
        this(url, encode, null, null);
    }
    
    public HttpExecutor(HttpUrl url, String usr, String passwd) {
        this(url, null, usr, passwd);
    }
    
    public HttpExecutor(HttpUrl url, String encode, String usr, String passwd) {
        this.url = url;
        this.encode = encode;
        HTTP_CLIENT = createThreadSafeClient();
        setUsernameAndPassword(usr, passwd);
        setConnectionTimeout(connectionTimeout);
    }

    public Locale getLocale() {
        return locale == null ? Locale.getDefault() : locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    public void setUsernameAndPassword(String usr, String passwd) {
        if (passwd == null) creds = null;
        else creds = new UsernamePasswordCredentials(usr, passwd);
    }
    
    public void wrapHttpClient() {
        wrapHttpClient(null, null, false);
    }
    
    public void wrapHttpClient(URL cacertFile, String cacertPasswd) {
        wrapHttpClient(cacertFile, cacertPasswd, true);
    }
    
    protected abstract HttpClientWrapper createHttpClientWrapper();
    
    private void wrapHttpClient(URL cacertFile, String cacertPasswd, boolean print) {
        String msg = "HTTPS cert check disabled.";
        HttpClientWrapper wrapper = createHttpClientWrapper();
        if (cacertFile != null) {
            try {
                wrapper.wrapHttpClient(false, cacertFile, cacertPasswd);
            }
            catch(Exception ex) {
                System.err.println(ex.getMessage());
                System.err.println(msg);
                wrapper.wrapHttpClient();
            }
        }
        else {
            if (print) System.out.println(msg);
            wrapper.wrapHttpClient();
        }
    }

    public HttpUrl getUrl() {
        return url;
    }

    public String getEncode() {
        if (encode == null) return "utf-8";
        return encode;
    }
    
    public void setEncode(String encode) {
        this.encode = encode;
    }
    
    public void setUrl(HttpUrl url) {
        if (url != null && !url.equals(this.url)) this.url = url;
    }
    
    public HttpStreamReturn execute() {
        return execute(url);
    }
    
    public HttpStreamReturn execute(HttpUrl url) {
        return execute(url, null);
    }
    
    public HttpResponseReturn getResponse() {
        return getResponse(url);
    }
    
    public HttpResponseReturn getResponse(HttpUrl url) {
        return getResponse(url, null);
    }
    
    public HttpResponseReturn getResponse(Map<String, List<String>> map) {
        return getResponse(url, map);
    }
    
    public HttpResponseReturn getResponse(HttpUrl url, Map<String, List<String>> map) {
        return getResponse(url, map, null);
    }
    
    public HttpResponseReturn getResponse(HttpUrl url, Map<String, List<String>> map, Integer timeout) {
        return getResponse(url, map, null, timeout);
    }
    
    public HttpResponseReturn getResponse(final HttpUrl url, final Map<String, List<String>> map, HttpEntity entity, Integer timeout) {
        HttpStreamReturn ret = entity == null ? execute(url, map, timeout) : execute(url, map, entity, timeout);
        if (ret.getException() != null) return new HttpResponseReturn(ret.getException());
        DataInputStream dis = new DataInputStream(ret.getStream());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte data;
        try {
            while ((data = dis.readByte()) != -1) {
                baos.write(data);
            }
        }
        catch(Exception ex) { //BUG FIX
            ;
        }
        byte[] response = baos.toByteArray();
        try {
            return new HttpResponseReturn(ret.getStatusCode(), new String(response, getEncode()));
        }
        catch(UnsupportedEncodingException ex) {
            return new HttpResponseReturn(ex);
        }
    }
    
    protected abstract AbstractHttpClient createThreadSafeClient();
    
    public AbstractHttpClient getHttpClient() {
        return HTTP_CLIENT;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int timeout) {
        setConnectionTimeout(HTTP_CLIENT, timeout);
    }
    
    public void setSocketTimeout(int timeout) {
        setSocketTimeout(HTTP_CLIENT, timeout);
    }
    
    public void setConnectionTimeout(HttpClient client, int timeout) {
        if (client != null) {
            client.getParams().setParameter(HttpConnectionParams.CONNECTION_TIMEOUT, timeout);
            this.connectionTimeout = timeout;
        }
    }
    
    public void setSocketTimeout(HttpClient client, int timeout) {
        if (client != null) {
            client.getParams().setParameter(HttpConnectionParams.SO_TIMEOUT, timeout);
        }
    }
    
    private FormEntityReturn createFormEntity(Map<String, List<String>> map) {
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (map != null) {
            Set<String> keys = map.keySet();
            for (String k : keys) {
                List<String> vals = map.get(k);
                for (String val : vals) {
                    nameValuePairs.add(new BasicNameValuePair(k, val));
                }
            }
        }
        try {
            return new FormEntityReturn(new UrlEncodedFormEntity(nameValuePairs, getEncode()));
        }
        catch (UnsupportedEncodingException ex) {
            return new FormEntityReturn(ex);
        }
    }
    
    private HttpStreamReturn getResponseStream(HttpResponse httpResponse) {
        HttpEntity httpEntity = httpResponse.getEntity();
        try {
            int status = httpResponse.getStatusLine().getStatusCode();
            return new HttpStreamReturn(status, httpEntity.getContent());
        }
        catch(IOException ex) {
            return new HttpStreamReturn(ex);
        }
    }
    
    public HttpStreamReturn execute(Map<String, List<String>> map) {
        return execute(url, map);
    }
    
    public HttpStreamReturn execute(Map<String, List<String>> map, int timeout) {
        return execute(url, map, timeout);
    }
    
    public HttpStreamReturn execute(final HttpUrl url, final Map<String, List<String>> map) {
        return execute(url, map, null);
    }
    
    public HttpStreamReturn execute(final HttpUrl url, final Map<String, List<String>> map, Integer timeout) {
        FormEntityReturn entityReturn = createFormEntity(map);
        if (entityReturn.getException() != null) return new HttpStreamReturn(entityReturn.getException());
        UrlEncodedFormEntity entity = entityReturn.getFormEntity();
        return execute(url, map, entity, timeout);
    }
    
    public HttpStreamReturn execute(final HttpUrl url, final Map<String, List<String>> map, HttpEntity entity, Integer timeout) {
        if (url == null) throw new NullPointerException("URL can not be null");
        HttpPost post = new HttpPost(url.toString());
        ExecuteData ed = new ExecuteData(url, map);
        EXECUTE_MAP.put(ed, post);
        if (entity != null) post.setEntity(entity);
        HttpStreamReturn ret;
        try {
            if (creds != null) post.addHeader(new BasicScheme().authenticate(creds, post));
            post.addHeader("Accept-Language", getLocale().getLanguage());
            Timer t = null;
            if (timeout != null) {
                t = new Timer();
                t.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        abort(url, map);
                    }
                    
                }, timeout);
            }
            HttpResponse response;
            try {
                response = HTTP_CLIENT.execute(post);
                if (t != null) t.cancel();
            }
            catch (RuntimeException ex) {
                if (t != null) t.cancel();
                throw ex;
            }
            ret = getResponseStream(response);
        }
        catch (Exception ex) {
            ret = new HttpStreamReturn(ex);
        }
        EXECUTE_MAP.remove(ed);
        return ret;
    }
    
    public void abortAll() {
        abort(null, null, true);
    }
    
    public void abort() {
        abort(url);
    }
    
    public void abort(HttpUrl url) {
        abort(url, null);
    }
    
    public void abort(Map<String, List<String>> map) {
        abort(url, map);
    }
    
    public void abort(HttpUrl url, Map<String, List<String>> map) {
        abort(url, map, false);
    }
    
    private void abort(HttpUrl url, Map<String, List<String>> map, boolean all) {
        ExecuteData ed = null;
        if (!all) ed = new ExecuteData(url, map);
        Iterator<Entry<ExecuteData, HttpPost>> it = EXECUTE_MAP.entrySet().iterator();
        while (it.hasNext()) {
            Entry<ExecuteData, HttpPost> e = it.next();
            if (all || e.getKey().equals(ed)) e.getValue().abort();
        }
    }
    
}