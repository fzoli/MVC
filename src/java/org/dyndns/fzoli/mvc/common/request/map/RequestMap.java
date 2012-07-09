package org.dyndns.fzoli.mvc.common.request.map;

import java.util.Map;
import org.dyndns.fzoli.map.ListMap;

/**
 *
 * @author zoli
 */
public class RequestMap extends ListMap<String, String> {

    public RequestMap() {
        super();
    }

    public RequestMap(RequestMap m) {
        super(m);
    }

    public RequestMap(Map<String, String[]> m) {
        super(m);
    }

    @Override
    public RequestMap setFirst(String key, String value) {
        return (RequestMap) super.setFirst(key, value);
    }
    
}