package org.dyndns.fzoli.mvc.server.model.bean;

import java.util.*;
import java.util.Map.Entry;
import javax.servlet.http.HttpSession;
import org.dyndns.fzoli.bean.AbstractBean;
import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.server.model.Model;
import org.dyndns.fzoli.mvc.server.model.map.ModelMap;

/**
 *
 * @author zoli
 */
public class ModelBean<EventType, PropsType> extends AbstractBean {
    
    private final HttpSession SESSION;
    private final ModelMap<Model<EventType, PropsType>> MODELS;
    
    /**
     * Beautiful constructor.
     * Sorry but ModelBean needs a map that countains Models.
     * @param modelMapClassName
     */
    public ModelBean(HttpSession session, ModelMap<Model<EventType, PropsType>> models) {
        models.setModelBean(this);
        SESSION = session;
        MODELS = models;
    }

    @Override
    public Date getExpireDate() {
        int timeout = SESSION.getMaxInactiveInterval();
        if (timeout <= 0) return null;
        try {
            return new Date(SESSION.getLastAccessedTime() + (1000 * timeout));
        }
        catch (Exception ex) {
            return new Date();
        }
    }

    public HttpSession getSession() {
        return SESSION;
    }

    @Override
    public void onExpire() {
        super.onExpire();
        MODELS.onExpire();
    }

    public ModelMap<Model<EventType, PropsType>> getModelMap() {
        return MODELS;
    }
    
    public boolean isEvent(String listenerId) {
        synchronized(MODELS) {
            List<Model<EventType, PropsType>> models = getRegistratedModels(listenerId);
            for (Model<EventType, PropsType> m: models) {
                if (m.isEvent(listenerId)) return true;
            }
            return false;
        }
    }
    
    private List<Model<EventType, PropsType>> getRegistratedModels(String listenerId) {
        synchronized(MODELS) {
            List<Model<EventType, PropsType>> models = new ArrayList<Model<EventType, PropsType>>();
            Iterator<Entry<String, Model<EventType, PropsType>>> it = MODELS.entrySet().iterator();
            while (it.hasNext()) {
                Model m = it.next().getValue();
                if (m.isListenerRegistrated(listenerId)) models.add(m);
            }
            return models;
        }
    }
    
    public EventMessage<EventType> getEvents(String listenerId) {
        synchronized(MODELS) {
            Map<String, List<EventType>> events = new HashMap<String, List<EventType>>();
            List<Model<EventType, PropsType>> models = getRegistratedModels(listenerId);
            for (Model<EventType, PropsType> m : models) {
                try {
                    String name = getModelName(m);
                    List<EventType> ev = m.safeGetEvents(listenerId);
                    if (!ev.isEmpty()) events.put(name, ev);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                    break;
                }
            }
            return new EventMessage<EventType>(events);
        }
    }
    
    public String getModelName(Model<EventType, PropsType> model) {
        synchronized(MODELS) {
            Iterator<Entry<String, Model<EventType, PropsType>>> it = MODELS.entrySet().iterator();
            while (it.hasNext()) {
                Entry<String, Model<EventType, PropsType>> e = it.next();
                if (e.getValue().equals(model)) return e.getKey();
            }
            return null;
        }
    }
    
    public Model<EventType, PropsType> getModel(String model) {
        return MODELS.get(model);
    }
    
    public Model<EventType, PropsType> getModel(String model, boolean init) {
        return MODELS.get(model, init);
    }
    
    public void addListener(String listenerId, String model) {
        synchronized(MODELS) {
            getModel(model).addListener(listenerId);
        }
    }
    
    public void removeListener(String listenerId, String model) {
        synchronized(MODELS) {
            getModel(model).removeListener(listenerId);
        }
    }
    
    public void removeListeners(String listenerId) {
        synchronized(MODELS) {
            Iterator<Entry<String, Model<EventType, PropsType>>> it = MODELS.entrySet().iterator();
            while (it.hasNext()) {
                it.next().getValue().removeListener(listenerId);
            }
        }
    }
    
    public boolean isListenerRegistrated(String listenerId) {
        synchronized(MODELS) {
            return !getRegistratedModels(listenerId).isEmpty();
        }
    }
    
}