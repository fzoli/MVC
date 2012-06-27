package org.dyndns.fzoli.jstl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 *
 * @author zoli
 */
public class SpanTagHandler extends SimpleTagSupport {
    
    private static final String NBSP = "&#160;";
    private String id, cls;

    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        try {
            JspFragment f = getJspBody();
            if (f != null) {
                f.invoke(out);
            }
            String sid = id == null ? "" : " id=\"" + id + "\"";
            String sclass = cls == null ? "" : " class=\"" + cls + "\"";
            out.print("<span" + sid + sclass + ">" + NBSP + "</span>");
        }
        catch (java.io.IOException ex) {
            throw new JspException("Error in MyTld tag", ex);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }
    
}