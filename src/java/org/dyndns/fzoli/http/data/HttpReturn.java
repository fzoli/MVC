package org.dyndns.fzoli.http.data;

import org.apache.http.HttpStatus;

/**
 *
 * @author zoli
 */
class HttpReturn<T> implements HttpStatus {
    
    private int status;
    private T data;
    private Exception ex;

    public HttpReturn(Exception ex) {
        this(0, null, ex);
    }
    
    public HttpReturn(int status, T data) {
        this(status, data, null);
    }

    private HttpReturn(int status, T data, Exception ex) {
        this.ex = ex;
        this.status = status;
        this.data = data;
    }

    public Exception getException() {
        return ex;
    }

    public int getStatusCode() {
        return status;
    }

    public boolean isStatusOk() {
        return getStatusCode() == SC_OK;
    }
    
    protected T getData() {
        return data;
    }
    
}