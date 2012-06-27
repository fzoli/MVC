package org.dyndns.fzoli.mvc.server.servlet.listener;

import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractJSONModelServlet;

/**
 *
 * @author zoli
 */
public class JSONListenerServlet extends AbstractJSONModelServlet implements ListenerServlet<Object, Object> {
    
    @Override
    public String eventMessageToString(EventMessage<Object> msg) {
        return getGson().toJson(msg);
    }
    
    @Override
    public String getServletInfo() {
        return "Model property-change listener using JSON.";
    }
    
}