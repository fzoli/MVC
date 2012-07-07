package org.dyndns.fzoli.mvc.server.model;

import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBeanRegister;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;

/**
 *
 * @author zoli
 */
public abstract class AbstractModel<EventType, PropsType, EventObj, PropsObj> implements Model<EventType, PropsType> {
    
    private String key;
    private ModelMap<Model<EventType, PropsType>> map;
    
    private final ModelEventRegister<EventType> REGISTER = new ModelEventRegister<EventType>();

    protected interface ModelIterator<EventType, PropsType> {
        
        void handler(String key, Model<EventType, PropsType> model);
        
    }
    
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ModelMap<Model<EventType, PropsType>> getModelMap() {
        return map;
    }
    
    @Override
    public void setLinks(ModelMap<Model<EventType, PropsType>> map, String key) {
        if (map != null && key != null) onPut();
        if (map == null && key == null) onRemove();
        this.map = map;
        this.key = key;
    }
    
    @Override
    public boolean isListenerRegistrated(String listenerId) {
        synchronized(REGISTER) {
            return REGISTER.isListenerIdExists(listenerId);
        }
    }
    
    @Override
    public void addListener(String listenerId) {
        synchronized(REGISTER) {
            REGISTER.addListenerId(listenerId);
        }
    }
    
    @Override
    public void removeListener(String listenerId) {
        synchronized(REGISTER) {
            REGISTER.removeListenerId(listenerId);
        }
    }
    
    @Override
    public boolean isEvent(String listenerId) {
        synchronized(REGISTER) {
            return REGISTER.isEvent(listenerId);
        }
    }
    
    @Override
    public final List<EventType> safeGetEvents(String listenerId) {
        synchronized(REGISTER) {
            return REGISTER.getEvents(listenerId);
        }
    }
    
    @Override
    public final PropsType safeGetProperties(HttpServletRequest servletRequest, RequestMap request) {
        synchronized(this) {
            return createProperties(getProperties(servletRequest, request));
        }
    }
    
    protected abstract EventType createEvent(EventObj o);
    
    protected abstract PropsType createProperties(PropsObj o);
    
    protected abstract PropsObj getProperties(HttpServletRequest servletRequest, RequestMap request);
    
    @Override
    public final int safeAskModel(HttpServletRequest servletRequest, RequestMap request) {
        synchronized(this) {
            return askModel(servletRequest, request);
        }
    }
    
    protected int askModel(HttpServletRequest servletRequest, RequestMap request) {
        return -1;
    }
    
    @Override
    public final RenderedImage safeGetImage(HttpServletRequest servletRequest, RequestMap request) {
        synchronized(this) {
            return getImage(servletRequest, request);
        }
    }
    
    protected RenderedImage getImage(HttpServletRequest servletRequest, RequestMap request) {
        return null;
    }
    
    @Override
    public final int safeSetImage(RenderedImage img, HttpServletRequest servletRequest, RequestMap request) {
        synchronized(this) {
            return setImage(img, servletRequest, request);
        }
    }
    
    protected int setImage(RenderedImage img, HttpServletRequest servletRequest, RequestMap request) {
        return -1;
    }
    
    @Override
    public final int safeSetProperty(HttpServletRequest servletRequest, RequestMap request) {
        synchronized(this) {
            return setProperty(servletRequest, request);
        }
    }
    
    protected abstract int setProperty(HttpServletRequest servletRequest, RequestMap request);
    
    protected void addEvent(EventObj ev) {
        synchronized(REGISTER) {
            REGISTER.addEvent(createEvent(ev));
        }
    }
    
    protected void addStaticEvent(EventObj ev) {
        if (getKey() == null) return;
        List<ModelBean> mbs = getModelBeans();
        synchronized (mbs) {
            for (ModelBean mb : mbs) {
                Model m = mb.getModel(getKey());
                if (m == null || !(m instanceof AbstractModel)) continue;
                AbstractModel am = (AbstractModel) m;
                am.addEvent(ev);
            }
        }
    }
    
    protected void onPut() {
        ;
    }
    
    protected void onRemove() {
        ;
    }
    
    protected static List<ModelBean> getModelBeans() {
        return ModelBeanRegister.getModelBeans();
    }
    
    protected static <T extends Model> T findModel(String key, Class<T> clazz) {
        return findModel(key, false, clazz);
    }
    
    protected static <T extends Model> T findModel(String key, boolean init, Class<T> clazz) {
        List<T> l = findModels(key, init, clazz);
        return l != null && !l.isEmpty() ? l.get(0) : null;
    }
    
    protected static <T extends Model> List<T> findModels(String key, Class<T> clazz) {
        return findModels(key, false, clazz);
    }
    
    protected static <T extends Model> List<T> findModels(String key, boolean init, Class<T> clazz) {
        if (key == null || clazz == null) return null;
        List<T> l = new ArrayList<T>();
        List<ModelBean> beans = getModelBeans();
        synchronized(beans) {
            for (ModelBean bean : beans) {
                Model m = bean.getModel(key, init);
                if (m == null) continue;
                try {
                    l.add((T)m);
                }
                catch (ClassCastException ex) {
                    ;
                }
            }
        }
        return l;
    }
    
    protected void iterateBeanModels(ModelIterator<EventType, PropsType> mi) {
        iterateModelMap(getModelMap(), mi);
    }
    
    protected void iterateEveryModel(ModelIterator<EventType, PropsType> mi) {
        List<ModelBean> beans = ModelBeanRegister.getModelBeans();
        for (ModelBean bean : beans) {
            try {
                iterateModelMap(bean.getModelMap(), mi);
            }
            catch (Exception ex) {
                ;
            }
        }
    }
    
    private void iterateModelMap(ModelMap m, ModelIterator<EventType, PropsType> i) {
        Iterator<Map.Entry<String, Model<EventType, PropsType>>> it = m.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Model<EventType, PropsType>> e = it.next();
            i.handler(e.getKey(), e.getValue());
        }
    }
    
}