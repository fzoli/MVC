package org.dyndns.fzoli.mvc.client.model.json;

import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.model.CachedModel;

/**
 *
 * @author zoli
 */
public abstract class CachedJSONModel<EventObj, PropsObj> extends CachedModel<Object, Object, EventObj, PropsObj> {

    public CachedJSONModel(Connection<Object, Object> connection, String modelKey, Class<EventObj> eventClass, Class<PropsObj> propsClass) {
        this(connection, modelKey, eventClass, propsClass, null);
    }

    public CachedJSONModel(Connection<Object, Object> connection, String modelKey, Class<EventObj> eventClass, Class<PropsObj> propsClass, ModelActionListener<PropsObj> action) {
        super(new JSONModel<EventObj, PropsObj>(connection, modelKey, eventClass, propsClass), action);
    }
    
}