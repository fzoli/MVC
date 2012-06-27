package org.dyndns.fzoli.mvc.client.event;

import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.connection.exception.ControllerCloseException;
import org.dyndns.fzoli.mvc.client.event.type.ModelActionEventType;
import org.dyndns.fzoli.mvc.client.model.Model;

/**
 *
 * @author zoli
 */
public class ModelActionEvent<T> extends ModelEvent<T> implements ModelActionEventType {
    
    public ModelActionEvent(Model source, T event) {
        super(source, event);
    }
    
    public ModelActionEvent(Model source, int type) {
        super(source, type);
    }

    public ModelActionEvent(Model source, ConnectionException ex) {
        super(source, ex);
    }
    
    public ModelActionEvent(Model source, ControllerCloseException ex) {
        super(source, TYPE_CONTROLLER_CLOSE_EXCEPTION, ex);
    }
    
    public ControllerCloseException getControllerCloseException() {
        return getException(ControllerCloseException.class);
    }
    
}