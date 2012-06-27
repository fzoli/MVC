package org.dyndns.fzoli.jstl;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class JavascriptTagHandler extends SimpleTagSupport {
    private String href;

    @Override
    public void doTag() throws JspException {
        JspWriter out = getJspContext().getOut();
        try {
            JspFragment f = getJspBody();
            if (f != null) {
                f.invoke(out);
            }
            out.print("<script type=\"text/javascript\" src=\"" + href + "\"></script>");
        }
        catch (java.io.IOException ex) {
            throw new JspException("Error in MyTld tag", ex);
        }
    }

    public void setHref(String href) {
        this.href = href;
    }
    
}