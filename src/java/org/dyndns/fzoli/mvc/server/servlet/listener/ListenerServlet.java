package org.dyndns.fzoli.mvc.server.servlet.listener;

import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.server.servlet.ModelServlet;

/**
 *
 * @author zoli
 */
public interface ListenerServlet<EventType, PropsType> extends ModelServlet<EventType, PropsType> {
    
    String PARAM_RECONNECT_WAIT = "reconnect_wait";
    String PARAM_EVENT_TIMEOUT = "event_timeout";
    String PARAM_EVENT_DELAY = "event_delay";
    String PARAM_GC_DELAY = "gc_delay";
    
    String eventMessageToString(EventMessage<EventType> msg);
    
}