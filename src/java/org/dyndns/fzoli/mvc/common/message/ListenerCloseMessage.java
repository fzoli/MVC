package org.dyndns.fzoli.mvc.common.message;

import org.dyndns.fzoli.mvc.common.message.key.ListenerReason;
import org.dyndns.fzoli.mvc.common.message.map.ListenerCloseMap;

/**
 *
 * @author zoli
 */
public class ListenerCloseMessage extends CloseMessage<ListenerCloseMap> implements ListenerReason {
    
    public ListenerCloseMessage(String reason, String listenerId, long initTime, int timeoutParameter, boolean newId) {
        super(new ListenerCloseMap(reason, listenerId, initTime, timeoutParameter, newId));
    }

    public ListenerCloseMessage(ListenerCloseMap messages) {
        super(messages);
    }
    
}