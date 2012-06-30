package org.dyndns.fzoli.mvc.server.model;

import java.awt.image.RenderedImage;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;

/**
 *
 * @author zoli
 */
public interface Model<EventType, PropsType> {
    
    String getKey();
    
    ModelMap<Model<EventType, PropsType>> getModelMap();
    
    void setLinks(ModelMap<Model<EventType, PropsType>> map, String key);
    
    boolean isEvent(String listenerId);
    
    boolean isListenerRegistrated(String listenerId);
    
    void addListener(String listenerId);
    
    void removeListener(String listenerId);
    
    List<EventType> safeGetEvents(String listenerId);
    
    PropsType safeGetProperties(HttpServletRequest servletRequest, RequestMap request);
    
    RenderedImage safeGetImage(HttpServletRequest servletRequest, RequestMap request);
    
    int safeSetImage(RenderedImage img, HttpServletRequest servletRequest, RequestMap request);
    
    int safeAskModel(HttpServletRequest servletRequest, RequestMap request);
    
    int safeSetProperty(HttpServletRequest servletRequest, RequestMap request);
    
}