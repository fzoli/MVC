package org.dyndns.fzoli.servlet;

import java.io.PrintWriter;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author zoli
 */
public interface PrinterServlet extends UserAgentServlet {
    
    PrintWriter getWriter(HttpServletResponse response);
    
}