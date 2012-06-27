package org.dyndns.fzoli.mvc.client.connection.exception;

import org.dyndns.fzoli.mvc.common.message.ControllerCloseMessage;
import org.dyndns.fzoli.mvc.common.message.key.ControllerReason;
import org.dyndns.fzoli.mvc.common.message.map.ControllerCloseMap;

/**
 *
 * @author zoli
 */
public class ControllerCloseException extends IllegalArgumentException implements ControllerReason {

    private ControllerCloseMessage msg;
    
    public ControllerCloseException(ControllerCloseMessage msg) {
        super(getReason(msg));
        this.msg = msg;
    }

    public String getReason() {
        return getReason(msg);
    }
    
    public ControllerCloseMap getMessages() {
        return msg == null ? null : msg.getMessages();
    }
    
    private static String getReason(ControllerCloseMessage msg) {
        if (msg != null && msg.getMessages() != null) return msg.getMessages().getReason();
        return null;
    }

}