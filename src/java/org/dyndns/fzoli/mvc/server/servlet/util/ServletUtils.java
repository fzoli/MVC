package org.dyndns.fzoli.mvc.server.servlet.util;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.dyndns.fzoli.mvc.common.request.map.ServletRequestMap;
import org.dyndns.fzoli.mvc.server.servlet.ModelServlet;
import org.dyndns.fzoli.mvc.server.servlet.controller.ControllerServlet;
import org.dyndns.fzoli.mvc.server.servlet.listener.ListenerIDRegister;
import org.dyndns.fzoli.mvc.server.servlet.listener.ListenerServlet;

/**
 *
 * @author zoli
 */
public abstract class ServletUtils<EventType, PropsType> {
    
    private final ModelServlet<EventType, PropsType> SERVLET;

    ServletUtils(ModelServlet<EventType, PropsType> servlet) {
        this.SERVLET = servlet;
    }

    protected ModelServlet<EventType, PropsType> getServlet() {
        return SERVLET;
    }
    
    protected void printString(HttpServletResponse response, String s) {
        getServlet().getWriter(response).println(s);
    }
    
    protected boolean isListenerIdExists(String id) {
        return ListenerIDRegister.isListenerIdExists(id);
    }
    
    protected int getConfigNumber(String name, int min, int max, int def) {
        try {
            return keepLimit(min, max, Integer.parseInt(getServlet().getInitParameter(name)));
        }
        catch (Exception ex) {
            return keepLimit(min, max, def);
        }
    }
    
    private int keepLimit(int min, int max, int val) {
        if (val <= min) return min;
        if (val >= max) return max;
        return val;
    }
    
    public abstract ServletRequestMap createRequestMap(HttpServletRequest request);
    
    public abstract void printResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    public static <EventType, PropsType> ServletUtils<EventType, PropsType> create(ModelServlet<EventType, PropsType> servlet) {
        if (servlet instanceof ControllerServlet)
            return new ControllerServletUtils<EventType, PropsType>((ControllerServlet<EventType, PropsType>)servlet);
        if (servlet instanceof ListenerServlet)
            return new ListenerServletUtils<EventType, PropsType>((ListenerServlet<EventType, PropsType>)servlet);
        throw new RuntimeException("This servlet doesn't implement ControllerServlet or ModelChangeListenerServlet.");
    }
    
}