package org.dyndns.fzoli.mvc.server.model;

import java.util.List;
import org.dom4j.dom.DOMDocument;
import org.dyndns.fzoli.mvc.common.key.XMLKeys;
import org.w3c.dom.Element;

/**
 *
 * @author zoli
 */
public abstract class XMLModel<EventObj, PropsObj> extends AbstractModel<Element, List<Element>, EventObj, PropsObj> {
    
    private static final DOMDocument doc = new DOMDocument();
    
    protected static Element createPropertyElement() {
        return createElement(XMLKeys.NODE_MODEL_EVENT_PROPERTY);
    }
    
    protected static Element createEventElement() {
        return createElement(XMLKeys.NODE_MODEL_EVENT);
    }
    
    private static Element createElement(String name) {
        return doc.createElement(name);
    }
    
}