package org.dyndns.fzoli.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractPrinterServlet extends AbstractServlet implements PrinterServlet {

    /**
     * Get PrintWriter.
     * @param response
     * @return Response's writer.
     */
    @Override
    public PrintWriter getWriter(HttpServletResponse response) {
        try {
            return response.getWriter();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    private void setContentType(HttpServletResponse response) {
        setUTF8ContentType(response, getContentType());
    }
    
    /**
     * @return Content-type.
     * For example: text/html
     */
    protected abstract String getContentType();
    
    /**
     * Processes requests and print response.
     * Before this method the default content-type were been applied.
     * After this method response's PrintWriter will be closed.
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * 
     * @see setDefaultContentType()
     */
    protected abstract void printResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
    
    /**
     * What have to do before servlet exit.
     */
    protected void dispose(HttpServletRequest request, HttpServletResponse response) {}
    
    @Override
    protected final void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setContentType(response);
        try {
            printResponse(request, response);
            dispose(request, response);
        }
        finally {
            try {
                getWriter(response).close();
            }
            catch (IllegalStateException ex) {
                ;
            }
        }
    }
    
}