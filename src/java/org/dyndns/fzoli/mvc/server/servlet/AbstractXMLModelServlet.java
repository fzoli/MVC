package org.dyndns.fzoli.mvc.server.servlet;

import java.util.Iterator;
import java.util.List;
import org.dom4j.dom.DOMDocument;
import org.dyndns.fzoli.key.DataType;
import org.dyndns.fzoli.mvc.common.message.CloseMessage;
import org.dyndns.fzoli.mvc.common.message.map.CloseMap;
import org.w3c.dom.Element;

/**
 *
 * @author zoli
 */
public abstract class AbstractXMLModelServlet extends AbstractModelServlet<Element, List<Element>> {
    
    @Override
    protected String getContentType() {
        return DataType.XML;
    }

    @Override
    public String closeMessageToString(CloseMessage msg) {
        DOMDocument doc = new DOMDocument();
        Element element = doc.createElement(msg.getType());
        doc.appendChild(element);
        CloseMap attributes = msg.getMessages();
        if (attributes != null) {
            Iterator<String> keys = attributes.keySet().iterator();
            while(keys.hasNext()) {
                String key = keys.next();
                String val = attributes.get(key);
                element.setAttribute(key, val);
            }
        }
        return doc.asXML();
    }
    
}