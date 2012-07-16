package org.dyndns.fzoli.mvc.server.servlet.controller;

import org.dyndns.fzoli.mvc.common.message.ModelMessage;
import org.dyndns.fzoli.mvc.common.message.ReturnMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractJSONModelServlet;
import org.dyndns.fzoli.mvc.server.servlet.util.ControllerServletUtils;

/**
 *
 * @author zoli
 */
public class JSONControllerServlet extends AbstractJSONModelServlet implements ControllerServlet<Object, Object> {

    @Override
    protected ControllerServletUtils<Object, Object> getServletUtils() {
        return (ControllerServletUtils<Object, Object>) super.getServletUtils();
    }
    
    @Override
    public String modelMessageToString(ModelMessage<Object> msg) {
        return getGson().toJson(msg);
    }

    @Override
    public String returnMessageToString(ReturnMessage msg) {
        return getGson().toJson(msg);
    }
    
    @Override
    public String getServletInfo() {
        return "Read/change models and register/unregister listeners with JSON format.";
    }
    
}