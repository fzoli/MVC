package org.dyndns.fzoli.mvc.server.servlet.listener;

import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractJSONModelServlet;
import org.dyndns.fzoli.mvc.server.servlet.util.ListenerServletUtils;

/**
 *
 * @author zoli
 */
public class JSONListenerServlet extends AbstractJSONModelServlet implements ListenerServlet<Object, Object> {

    @Override
    protected ListenerServletUtils<Object, Object> getServletUtils() {
        return (ListenerServletUtils<Object, Object>) super.getServletUtils();
    }
    
    @Override
    public String eventMessageToString(EventMessage<Object> msg) {
        return getGson().toJson(msg);
    }
    
    @Override
    public String getServletInfo() {
        return "Model property-change listener using JSON.";
    }
    
}