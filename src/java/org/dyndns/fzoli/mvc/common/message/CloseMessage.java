package org.dyndns.fzoli.mvc.common.message;

import org.dyndns.fzoli.mvc.common.message.map.CloseMap;

/**
 *
 * @author zoli
 */
public class CloseMessage<CloseMapType extends CloseMap> extends ServletMessage {
    
    private CloseMapType messages;

    public static final String TYPE = "closed";
    
    public CloseMessage(CloseMapType messages) {
        super(TYPE);
        this.messages = messages;
    }

    public CloseMapType getMessages() {
        return messages;
    }
    
    @Override
    public String toString() {
        return getMessages().toString();
    }
    
}