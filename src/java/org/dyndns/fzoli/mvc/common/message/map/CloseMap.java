package org.dyndns.fzoli.mvc.common.message.map;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author zoli
 */
public class CloseMap extends HashMap<String, String> {

    private static final String KEY_REASON = "reason";

    public CloseMap(String reason) {
        put(KEY_REASON, reason);
    }
    
    public CloseMap(Map<? extends String, ? extends String> m) {
        super(m);
    }
    
    public String getReason() {
        return get(KEY_REASON);
    }
    
}