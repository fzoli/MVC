package org.dyndns.fzoli.mvc.client.event;

import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.event.type.ModelChangeEventType;
import org.dyndns.fzoli.mvc.client.model.Model;

/**
 *
 * @author zoli
 */
public class ModelChangeEvent<T> extends ModelEvent<T> implements ModelChangeEventType {
    
    private boolean reset;
    
    public ModelChangeEvent(Model source, int type, boolean reset) {
        super(source, type);
        this.reset = reset;
    }
    
    public ModelChangeEvent(Model source, T event, boolean reset) {
        super(source, event);
        this.reset = reset;
    }

    public ModelChangeEvent(Model source, ConnectionException ex, boolean reset) {
        super(source, ex);
        this.reset = reset;
    }
    
    public boolean isReset() {
        return reset;
    }
    
}