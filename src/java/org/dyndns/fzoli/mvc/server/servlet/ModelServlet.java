package org.dyndns.fzoli.mvc.server.servlet;

import javax.servlet.http.HttpServletRequest;
import org.dyndns.fzoli.mvc.common.message.CloseMessage;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;
import org.dyndns.fzoli.servlet.PrinterServlet;

/**
 *
 * @author zoli
 */
public interface ModelServlet<EventType, PropsType> extends PrinterServlet {
    
    ModelBean<EventType, PropsType> getModelBean(HttpServletRequest request);
    
    ModelMap getModelMap(HttpServletRequest request);
    
    String closeMessageToString(CloseMessage msg);
    
}