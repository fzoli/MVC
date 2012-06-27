package org.dyndns.fzoli.mvc.common.request.map;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zoli
 */
public class ServletRequestMap extends RequestMap {

    private static final String LISTENER_ID = "listener_id";
    
    private RequestMap values = new RequestMap();

    public ServletRequestMap() {
        super();
    }
    
    public ServletRequestMap(String listenerId) {
        super();
        setListenerId(listenerId);
    }
    
    public ServletRequestMap(Map<String, String[]> m) {
        super(m);
        initValue(LISTENER_ID);
    }
    
    public final String getListenerId() {
        return values.getFirst(LISTENER_ID);
    }
    
    private void setListenerId(String listenerId) {
        setValue(LISTENER_ID, listenerId);
    }
    
    protected final List<String> getValues(String key) {
        return values.get(key);
    }
    
    protected final String getValue(String key) {
        return values.getFirst(key);
    }
    
    protected final void setValues(String key, List<String> value) {
        values.put(key, value);
        put(key, value);
    }
    
    protected final void setValue(String key, String value) {
        values.setFirst(key, value);
        setFirst(key, value);
    }
    
    protected final void initValue(String key) {
        values.put(key, get(key));
        remove(key);
    }
    
}