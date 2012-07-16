package org.dyndns.fzoli.mvc.server.servlet.listener;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.dom4j.dom.DOMDocument;
import org.dyndns.fzoli.mvc.common.key.XMLKeys;
import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractXMLModelServlet;
import org.dyndns.fzoli.mvc.server.servlet.util.ListenerServletUtils;
import org.w3c.dom.Element;

/**
 *
 * @author zoli
 */
public class XMLListenerServlet extends AbstractXMLModelServlet implements ListenerServlet<Element, List<Element>> {

    @Override
    protected ListenerServletUtils<Element, List<Element>> getServletUtils() {
        return (ListenerServletUtils<Element, List<Element>>) super.getServletUtils();
    }

    @Override
    public String eventMessageToString(EventMessage<Element> msg) {
        Map<String, List<Element>> evts = msg.getEvents();
        DOMDocument doc = new DOMDocument();
        Element event = doc.createElement(msg.getType());
        doc.appendChild(event);
        Iterator<String> names = evts.keySet().iterator();
        while(names.hasNext()) {
            String name = names.next();
            List<Element> l = evts.get(name);
            Element m = doc.createElement(XMLKeys.NODE_GET_MODEL);
            event.appendChild(m);
            m.setAttribute(XMLKeys.PARAM_NAME, name);
            for (Element e : l) {
                m.appendChild(e);
            }
        }
        return doc.asXML();
    }
    
    @Override
    public String getServletInfo() {
        return "Model property-change listener using XML.";
    }
    
}