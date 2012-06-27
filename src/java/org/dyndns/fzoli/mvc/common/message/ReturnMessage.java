package org.dyndns.fzoli.mvc.common.message;

/**
 *
 * @author zoli
 */
public class ReturnMessage extends ServletMessage {
    
    private String value;

    public static final String TYPE = "return";
    
    public ReturnMessage(String value) {
        super(TYPE);
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return getValue();
    }

}