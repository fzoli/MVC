package org.dyndns.fzoli.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;

class CountingOutputStream extends OutputStream {
    
    private final OutputStream STREAM;
    private final CountingListener LISTENER;
    private final CountingHttpEntity ENTITY;

    private long transferred = 0;
    
    public CountingOutputStream(CountingHttpEntity entity, OutputStream stream, CountingListener listener) {
        ENTITY = entity;
        STREAM = stream;
        LISTENER = listener;
    }

    @Override
    public void write(int b) throws IOException {
        STREAM.write(b);
        if (LISTENER != null) LISTENER.onWrite(ENTITY.getContentLength(), ++transferred);
    }
    
}

/**
 *
 * @author zoli
 */
public class CountingHttpEntity implements HttpEntity {

    private final HttpEntity DELEGATE;
    private final CountingListener LISTENER;

    public CountingHttpEntity(HttpEntity delegate, CountingListener listener) {
        if (delegate == null) throw new NullPointerException("HttpEntity is null");
        DELEGATE = delegate;
        LISTENER = listener;
    }
    
    @Override
    public boolean isRepeatable() {
        return DELEGATE.isRepeatable();
    }

    @Override
    public boolean isChunked() {
        return DELEGATE.isChunked();
    }

    @Override
    public long getContentLength() {
        return DELEGATE.getContentLength();
    }

    @Override
    public Header getContentType() {
        return DELEGATE.getContentType();
    }

    @Override
    public Header getContentEncoding() {
        return DELEGATE.getContentEncoding();
    }

    @Override
    public InputStream getContent() throws IOException, IllegalStateException {
        return DELEGATE.getContent();
    }

    @Override
    public void writeTo(OutputStream out) throws IOException {
        DELEGATE.writeTo(new CountingOutputStream(this, out, LISTENER));
    }

    @Override
    public boolean isStreaming() {
        return DELEGATE.isStreaming();
    }

    @Override
    public void consumeContent() throws IOException {
        DELEGATE.consumeContent();
    }
    
}