package org.dyndns.fzoli.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.bitwalker.useragentutils.UserAgent;

/**
 *
 * @author zoli
 */
public abstract class AbstractServlet extends HttpServlet implements UserAgentServlet {

    public static final String PARAM_REDIRECT = "redirect";
    public static final String PARAM_REDIRECT_PATH = "redirect_path";
    
    /**
     * Processes requests.
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;

    /**
     * Redirect to the redirect path that specified in the web.xml configuration file.
     * If the redirect path is not specified it will redirect to the servlet's directory.
     * 
     * @throws  IOException if an I/O error occurs during printing message.
     */
    protected void redirect(HttpServletResponse response) throws IOException {
        redirect(response, getRedirectPath());
    }

    private boolean isRedirect() {
        return Boolean.parseBoolean(findInitParameter(PARAM_REDIRECT, Boolean.toString(true)));
    }
    
    private String getRedirectPath() {
        return findInitParameter(PARAM_REDIRECT_PATH, "./");
    }
    
    @Override
    public UserAgent getUserAgent(HttpServletRequest request) {
        return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
    }
    
    protected String findInitParameter(String name) {
        return findInitParameter(name, null);
    }
    
    protected String findInitParameter(String name, String def) {
        try {
            String s = getInitParameter(name);
            if (s == null) s = getCtxInitParameter(name);
            if (s == null) return def;
            else return s;
        }
        catch (Exception ex) {
            System.err.println("AbstractServlet.findInitParameter - " + ex.getMessage());
            return def;
        }
    }
    
    protected String getCtxInitParameter(String name) {
        return getCtxInitParameter(name, null);
    }
    
    protected String getCtxInitParameter(String name, String def) {
        String s = getServletContext().getInitParameter(name);
        return s == null ? def : s;
    }
    
    protected Object getSessionAttribute(HttpServletRequest request, String name) {
        return request.getSession(true).getAttribute(name);
    }
    
    protected void setSessionAttribute(HttpServletRequest request, String name, Object value) {
        request.getSession(true).setAttribute(name, value);
    }
    
    /**
     * Redirect to the given URL.
     */
    protected void redirect(HttpServletResponse response, String url) {
        try {
            response.sendRedirect(url);
        }
        catch(IOException ex) {}
    }

    protected void setUTF8ContentType(HttpServletResponse response, String value) {
        response.setContentType(value + ";charset=utf-8");
    }

    private void setUTF8CharacterEncoding(HttpServletRequest request) {
        try {
            request.setCharacterEncoding("utf-8");
        }
        catch(UnsupportedEncodingException ex) {}
    }
    
    private void initServlet(HttpServletRequest request) {
        setUTF8CharacterEncoding(request);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods.">
    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected final void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (isRedirect()) redirect(response);
        else doRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected final void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doRequest(request, response);
    }

    private void doRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        initServlet(request);
        setRedirectPath(request, response);
        processRequest(request, response);
    }
    
    private void setRedirectPath(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String s = request.getParameter(PARAM_REDIRECT_PATH);
        if (s != null) redirect(response, s);
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public abstract String getServletInfo();
    // </editor-fold>

}