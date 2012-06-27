package org.dyndns.fzoli.mvc.server.model;

import java.util.*;

/**
 *
 * @author zoli
 */
public class ModelEventRegister<EventType> {
    
    private final Map<String, List<EventType>> EVENT_MAP = new HashMap<String, List<EventType>>();
    
    public void addListenerId(String listenerId) {
        EVENT_MAP.put(listenerId, new ArrayList<EventType>());
    }
    
    public void removeListenerId(String listenerId) {
        EVENT_MAP.remove(listenerId);
    }
    
    public void addEvent(EventType e) {
        Iterator<String> keys = EVENT_MAP.keySet().iterator();
        while (keys.hasNext()) {
            List<EventType> eventList = EVENT_MAP.get(keys.next());
            eventList.add(e);
        }
    }
    
    public boolean isListenerIdExists(String listenerId) {
        return EVENT_MAP.containsKey(listenerId);
    }
    
    public boolean isEvent(String listenerId) {
        return !EVENT_MAP.get(listenerId).isEmpty();
    }

    public List<EventType> getEvents(String listenerId) {
        List<EventType> e = EVENT_MAP.get(listenerId);
        List<EventType> l = new ArrayList<EventType>(e);
        e.clear();
        return l;
    }
    
}