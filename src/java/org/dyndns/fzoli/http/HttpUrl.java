package org.dyndns.fzoli.http;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Farkas Zoltán
 */
public final class HttpUrl {
    
    private boolean ssl;
    private Integer port;
    private String host, path, params;

    public HttpUrl(String host) {
        this(false, host);
    }
    
    public HttpUrl(String host, Integer port) {
        this(false, host, port);
    }
    
    public HttpUrl(String host, String path) {
        this(false, host, path);
    }
    
    public HttpUrl(String host, Integer port, String path) {
        this(false, host, port, path);
    }
    
    public HttpUrl(boolean ssl, String host) {
        this(ssl, host, null, null);
    }
    
    public HttpUrl(boolean ssl, String host, Integer port) {
        this(ssl, host, port, null);
    }
    
    public HttpUrl(boolean ssl, String host, String path) {
        this(ssl, host, null, path);
    }
    
    public HttpUrl(boolean ssl, String host, Integer port, String path) {
        setHost(host);
        setPort(port);
        setSsl(ssl);
        setPath(path);
    }

    public HttpUrl(HttpUrl url) {
        this(url.ssl, url.host, url.port, url.path);
    }
    
    public HttpUrl(HttpUrl url, Map<String, List<String>> m) {
        this(url);
        setPath(path, m);
    }
    
    public void setPath(String path) {
        setPath(path, null);
    }
    
    public void setPath(String path, Map<String, List<String>> m) {
        this.params = requestMapToString(m);
        this.path = path;
    }
    
    public void setHost(String host) {
        if (host == null) throw new NullPointerException("Host can not be null");
        this.host = host;
    }
    
    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }
    
    public void setPort(Integer port) {
        this.port = port;
    }
    
    @Override
    public String toString() {
        return "http" + (isSsl() ? "s" : "") + "://" + getHost() + ":" + getPort() + "/" + safe(getPath()) + safe(getParams());
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj != null && getClass() == obj.getClass()) return equals((HttpUrl) obj);
        return false;
    }

    private boolean equals(HttpUrl cd) {
        return getPort() == cd.getPort() && isSsl() == cd.isSsl() && equals(getHost(), cd.getHost()) && equals(getPath(), cd.getPath());
    }

    private boolean equals(String s1, String s2) {
        if (s1 == null || s2 == null) return false;
        return s1.equals(s2);
    }
    
    public String getHost() {
        return host;
    }

    public int getPort() {
        return port == null ? isSsl() ? 443 : 80 : port;
    }

    public boolean isSsl() {
        return ssl;
    }

    public String getPath() {
        if (path == null) return "";
        return path;
    }

    public String getParams() {
        if (params == null) return "";
        return params;
    }
    
    public static String requestMapToString(Map<String, List<String>> m) {
        if (m == null) return null;
        Iterator<Map.Entry<String, List<String>>> it = m.entrySet().iterator();
        StringBuilder sb = new StringBuilder("?");
        while (it.hasNext()) {
            Map.Entry<String, List<String>> e = it.next();
            List<String> l = e.getValue();
            if (l == null) continue;
            for (int i = 0; i < l.size(); i++) {
                sb.append(e.getKey());
                sb.append('=');
                sb.append(l.get(i));
                if (i != l.size() - 1) sb.append('&');
            }
            if (it.hasNext()) sb.append('&');
        }
        return sb.toString();
    }
    
}