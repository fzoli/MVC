package org.dyndns.fzoli.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import nl.bitwalker.useragentutils.UserAgent;

/**
 *
 * @author zoli
 */
public interface UserAgentServlet extends Servlet, ServletConfig {
    
    UserAgent getUserAgent(HttpServletRequest request);
    
}