package org.dyndns.fzoli.mvc.common.message.map;

import java.util.Map;

/**
 *
 * @author zoli
 */
public class ControllerCloseMap extends CloseMap {

    public ControllerCloseMap(String reason) {
        super(reason);
    }

    public ControllerCloseMap(Map<? extends String, ? extends String> m) {
        super(m);
    }
    
}