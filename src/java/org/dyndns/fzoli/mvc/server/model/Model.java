package org.dyndns.fzoli.mvc.server.model;

import java.awt.image.RenderedImage;
import java.util.List;
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
    
    PropsType safeGetProperties(RequestMap request);
    
    RenderedImage safeGetImage(RequestMap request);
    
    int safeSetImage(RenderedImage img, RequestMap request);
    
    int safeAskModel(RequestMap request);
    
    int safeSetProperty(RequestMap request);
    
}