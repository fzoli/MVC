package org.dyndns.fzoli.http.data;

import java.io.InputStream;

/**
 *
 * @author zoli
 */
public class HttpStreamReturn extends HttpReturn<InputStream> {

    public HttpStreamReturn(Exception ex) {
        super(ex);
    }

    public HttpStreamReturn(int status, InputStream stream) {
        super(status, stream);
    }
    
    public InputStream getStream() {
        return getData();
    }
    
}
