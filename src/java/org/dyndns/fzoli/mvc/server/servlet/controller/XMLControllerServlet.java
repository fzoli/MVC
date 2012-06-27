package org.dyndns.fzoli.mvc.server.servlet.controller;

import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.dom4j.dom.DOMDocument;
import org.dyndns.fzoli.mvc.common.key.XMLKeys;
import org.dyndns.fzoli.mvc.common.message.ModelMessage;
import org.dyndns.fzoli.mvc.common.message.ReturnMessage;
import org.dyndns.fzoli.mvc.server.servlet.AbstractXMLModelServlet;
import org.w3c.dom.Element;

public class XMLControllerServlet extends AbstractXMLModelServlet implements ControllerServlet<Element, List<Element>> {

    @Override
    public String modelMessageToString(ModelMessage<List<Element>> msg) {
        Iterator<Entry<String, List<Element>>> it = msg.getModels().entrySet().iterator();
        DOMDocument doc = new DOMDocument();
        Element root = doc.createElement(msg.getType());
        doc.appendChild(root);
        while (it.hasNext()) {
            Entry<String, List<Element>> en = it.next();
            Element me = doc.createElement(XMLKeys.NODE_GET_MODEL);
            me.setAttribute(XMLKeys.PARAM_NAME, en.getKey());
            List<Element> es = en.getValue();
            for (Element e : es) {
                me.appendChild(e);
            }
            root.appendChild(me);
        }
        return doc.asXML();
    }

    @Override
    public String returnMessageToString(ReturnMessage msg) {
        DOMDocument doc = new DOMDocument();
        Element e = doc.createElement(msg.getType());
        doc.appendChild(e);
        e.setAttribute(XMLKeys.PARAM_VALUE, msg.getValue());
        return doc.asXML();
    }

    @Override
    public String getServletInfo() {
        return "Read/change models and register/unregister listeners with XML format.";
    }

}