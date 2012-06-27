package org.dyndns.fzoli.mvc.client.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;
import org.dyndns.fzoli.mvc.client.event.ModelChangeListener;
import org.dyndns.fzoli.mvc.client.event.ModelStateListener;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public interface Model<EventType, PropsType, EventObj, PropsObj> extends BaseModel<EventType, PropsType> {
    
    boolean isOptimized();
    
    PropsObj getProperties();
    
    PropsObj getProperties(RequestMap map);
    
    int askModel();
    
    int askModel(RequestMap map);
    
    InputStream getImage(RequestMap map);
    
    int setImage(ByteArrayOutputStream os, RequestMap map);
    
    int setProperty(RequestMap map);
    
    void getProperties(ModelActionListener<PropsObj> action);
    
    void getProperties(final RequestMap map, ModelActionListener<PropsObj> action);
    
    void askModel(ModelActionListener<Integer> action);
    
    void askModel(final RequestMap map, ModelActionListener<Integer> action);
    
    void getImage(final RequestMap map, ModelActionListener<InputStream> action);
    
    void setProperty(final RequestMap map, ModelActionListener<Integer> action);
    
    boolean setListenerEnabled(boolean enable);
    
    boolean isListening();
    
    List<ModelChangeListener<EventObj>> getListeners();
    
    boolean addListener(ModelChangeListener<EventObj> l);
    
    boolean removeListener(ModelChangeListener<EventObj> l);
    
    void addStateListener(ModelStateListener l);
    
    void removeStateListener(ModelStateListener l);
    
}