package org.dyndns.fzoli.mvc.client.connection.exception;

import org.apache.http.HttpStatus;
import org.dyndns.fzoli.http.data.HttpResponseReturn;

/**
 *
 * @author zoli
 */
public class ConnectionException extends RuntimeException implements HttpStatus {

    private HttpResponseReturn response;
    
    public ConnectionException(HttpResponseReturn response) {
        super("HTTP (" + response.getStatusCode() + ")");
        this.response = response;
    }

    public HttpResponseReturn getHttpResponse() {
        return response;
    }
    
    public boolean isConnectionError() {
        return getHttpException() != null;
    }

    public Exception getHttpException() {
        return getHttpResponse().getException();
    }
    
    @Override
    public String getMessage() {
        return isConnectionError() ? getHttpException().getMessage() : super.getMessage();
    }
    
}