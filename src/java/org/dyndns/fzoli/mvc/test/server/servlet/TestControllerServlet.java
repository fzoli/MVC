package org.dyndns.fzoli.mvc.test.server.servlet;

import javax.servlet.annotation.WebServlet;
import org.dyndns.fzoli.mvc.server.servlet.controller.JSONControllerServlet;

/**
 *
 * @author zoli
 */
@WebServlet(urlPatterns={"/Controller"})
public final class TestControllerServlet extends JSONControllerServlet {
    
}