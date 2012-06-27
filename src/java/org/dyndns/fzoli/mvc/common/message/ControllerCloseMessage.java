package org.dyndns.fzoli.mvc.common.message;

import org.dyndns.fzoli.mvc.common.message.key.ControllerReason;
import org.dyndns.fzoli.mvc.common.message.map.ControllerCloseMap;

/**
 *
 * @author zoli
 */
public class ControllerCloseMessage extends CloseMessage<ControllerCloseMap> implements ControllerReason {
    
    public ControllerCloseMessage(String reason) {
        super(new ControllerCloseMap(reason));
    }

    public ControllerCloseMessage(ControllerCloseMap messages) {
        super(messages);
    }
    
}