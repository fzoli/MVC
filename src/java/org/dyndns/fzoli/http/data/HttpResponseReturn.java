package org.dyndns.fzoli.http.data;

/**
 *
 * @author zoli
 */
public class HttpResponseReturn extends HttpReturn<String> {

    public HttpResponseReturn(Exception ex) {
        super(ex);
    }

    public HttpResponseReturn(int status, String response) {
        super(status, response);
    }
    
    public String getResponse() {
        return getData();
    }
    
}