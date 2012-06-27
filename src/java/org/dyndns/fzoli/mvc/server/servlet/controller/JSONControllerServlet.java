package org.dyndns.fzoli.mvc.server.servlet.controller;

import org.dyndns.fzoli.mvc.common.message.ModelMessage;
import org.dyndns.fzoli.mvc.common.message.ReturnMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractJSONModelServlet;

/**
 *
 * @author zoli
 */
public class JSONControllerServlet extends AbstractJSONModelServlet implements ControllerServlet<Object, Object> {
    
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