package org.dyndns.fzoli.mvc.client.model.json;

import com.google.gson.Gson;
import java.lang.reflect.ParameterizedType;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.model.AbstractModel;

/**
 *
 * @author zoli
 */
public class JSONModel<EventObj, PropsObj> extends AbstractModel<Object, Object, EventObj, PropsObj> {

    private static final int POSSIBLE_EVENT_OBJ = 0;
    private static final int POSSIBLE_PROPS_OBJ = 1;
    private static final Gson GSON = new Gson();
    
    private Class<EventObj> eventClass;
    private Class<PropsObj> propsClass;
    
    @Deprecated
    public JSONModel(Connection<Object, Object> connection, String modelKey) {
        super(connection, modelKey);
    }
    
    public JSONModel(Connection<Object, Object> connection, String modelKey, Class<EventObj> eventClass, Class<PropsObj> propsClass) {
        super(connection, modelKey);
        this.eventClass = eventClass;
        this.propsClass = propsClass;
    }
    
    @Override
    protected EventObj createEvent(Object e) {
        return createObject(e, (Class<EventObj>)getEventClass());
    }

    @Override
    protected PropsObj createProperties(Object p) {
        return createObject(p, getPropsClass());
    }
    
    protected Class getGenericClass() {
        return getClass();
    }
    
    protected Class<EventObj> getEventClass() {
        return eventClass == null ? (Class<EventObj>)createClass(POSSIBLE_EVENT_OBJ) : eventClass;
    }

    protected Class<PropsObj> getPropsClass() {
        return propsClass == null ? (Class<PropsObj>)createClass(POSSIBLE_PROPS_OBJ) : propsClass;
    }
    
    protected <T> T createObject(Object o, Class<T> c) {
        return GSON.fromJson(GSON.toJsonTree(o), c);
    }
    
    private Class createClass(int index) {
        try {
            return (Class)((ParameterizedType)getGenericClass().getGenericSuperclass()).getActualTypeArguments()[index];
        }
        catch (Exception ex) {
            return Object.class;
        }
    }
    
}