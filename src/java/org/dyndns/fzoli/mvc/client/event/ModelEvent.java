package org.dyndns.fzoli.mvc.client.event;

import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.event.type.ModelEventType;
import org.dyndns.fzoli.mvc.client.model.Model;

/**
 *
 * @author zoli
 */
public class ModelEvent<T> implements CommonEvent<T>, ModelEventType {
    
    private int type;
    private T event;
    private Exception ex;
    private Model<?, ?, ?, ?> srcModel;
    
    public ModelEvent(Model source, int type) {
        this(source, type, null, null);
    }
    
    public ModelEvent(Model source, T event) {
        this(source, TYPE_EVENT, event, null);
    }

    public ModelEvent(Model source, ConnectionException ex) {
        this(source, TYPE_CONNECTION_EXCEPTION, ex);
    }

    protected ModelEvent(Model source, int type, Exception ex) {
        this(source, type, null, ex);
    }
    
    private ModelEvent(Model source, int type, T event, Exception ex) {
        this.srcModel = source;
        this.type = type;
        this.event = event;
        this.ex = ex;
    }
    
    @Override
    public Model<?, ?, ?, ?> getSourceModel() {
        return srcModel;
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public T getEvent() {
        return event;
    }
    
    public ConnectionException getConnectionException() {
        return getException(ConnectionException.class);
    }
    
    protected <T extends Exception> T getException(Class<T> clazz) {
        if (clazz != null && clazz.isInstance(ex)) return (T) ex;
        return null;
    }
    
}