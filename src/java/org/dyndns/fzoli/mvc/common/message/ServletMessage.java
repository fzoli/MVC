package org.dyndns.fzoli.mvc.common.message;

/**
 *
 * @author zoli
 */
public class ServletMessage {
    
    private String type;

    public ServletMessage(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
    
}