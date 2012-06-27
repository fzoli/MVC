package org.dyndns.fzoli.mvc.common.message.map;

import java.util.Map;

/**
 *
 * @author zoli
 */
public class ListenerCloseMap extends CloseMap {

    private static final String KEY_NEW_ID = "new_id";
    private static final String KEY_INIT_TIME = "init_time";
    private static final String KEY_LISTENER_ID = "listener_id";
    private static final String KEY_TIMEOUT_PARAMETER = "timeout_parameter";
    
    public ListenerCloseMap(String reason, String listenerId, long initTime, int timeoutParameter, boolean newId) {
        super(reason);
        put(KEY_LISTENER_ID, listenerId);
        put(KEY_NEW_ID, Boolean.toString(newId));
        put(KEY_INIT_TIME, Long.toString(initTime));
        put(KEY_TIMEOUT_PARAMETER, Integer.toString(timeoutParameter));
    }

    public ListenerCloseMap(Map<? extends String, ? extends String> m) {
        super(m);
    }
    
    public int getTimeoutParameter() {
        return Integer.parseInt(get(KEY_TIMEOUT_PARAMETER));
    }
    
    public long getInitTime() {
        return Long.parseLong(get(KEY_INIT_TIME));
    }
    
    public String getListenerId() {
        return get(KEY_LISTENER_ID);
    }
    
    public boolean isNewId() {
        return Boolean.parseBoolean(get(KEY_NEW_ID));
    }
    
}