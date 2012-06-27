package org.dyndns.fzoli.mvc.test.server.servlet;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import org.dyndns.fzoli.mvc.server.servlet.listener.JSONListenerServlet;

/**
 *
 * @author zoli
 */
@WebServlet(
        urlPatterns={"/ChangeListener"}, 
        initParams ={
            @WebInitParam(name=TestListenerServlet.PARAM_EVENT_DELAY, value="50"),
            @WebInitParam(name=TestListenerServlet.PARAM_EVENT_TIMEOUT, value="20000"),
            @WebInitParam(name=TestListenerServlet.PARAM_GC_DELAY, value="60000")
        }
)
public final class TestListenerServlet extends JSONListenerServlet {
    
}