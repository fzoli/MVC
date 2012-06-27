package org.dyndns.fzoli.mvc.common.message;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zoli
 */
public class EventMessage<EventType> extends ServletMessage {
    
    private Map<String, List<EventType>> events;

    public static final String TYPE = "events";
    
    public EventMessage(Map<String, List<EventType>> events) {
        super(TYPE);
        this.events = events;
    }

    public Map<String, List<EventType>> getEvents() {
        return events;
    }

    @Override
    public String toString() {
        return getEvents().toString();
    }
    
}