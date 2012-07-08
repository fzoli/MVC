package org.dyndns.fzoli.mvc.client.model;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.connection.exception.ControllerCloseException;
import org.dyndns.fzoli.mvc.client.event.*;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;

/**
 *
 * @author zoli
 */
public abstract class AbstractModel<EventType, PropsType, EventObj, PropsObj> implements Model<EventType, PropsType, EventObj, PropsObj> {
    
    private final String KEY;
    private final Connection<EventType, PropsType> CONNECTION;
    private final List<ModelStateListener> STATE_LISTENERS = new ArrayList<ModelStateListener>();
    private final List<ModelChangeListener<EventObj>> LISTENERS = new ArrayList<ModelChangeListener<EventObj>>();

    protected abstract class CallbackAction<T> implements ModelActionListener<T> {

        private ModelActionListener<T> l, l2;

        public CallbackAction(ModelActionListener<T> l) {
            this.l = l;
        }
        
        public CallbackAction(ModelActionListener<T> l, ModelActionListener<T> l2) {
            this.l = l;
            this.l2 = l2;
        }
        
        protected abstract T run();

        @Override
        public void modelActionPerformed(ModelActionEvent<T> e) {
            if (l != null) l.modelActionPerformed(e);
            if (l2 != null && e.getType() == ModelActionEvent.TYPE_EVENT) l2.modelActionPerformed(e);
        }

    }
    
    public AbstractModel(Connection<EventType, PropsType> connection, String modelKey) {
        this.KEY = modelKey;
        this.CONNECTION = connection;
        if (!isOptimized()) CONNECTION.addListener(this);
    }

    public AbstractModel(BaseModel<EventType, PropsType> model) {
        this(model.getConnection(), model.getKey());
    }

    @Override
    protected void finalize() throws Throwable {
        if (!isOptimized()) CONNECTION.removeListener(this);
        super.finalize();
    }
    
    @Override
    public Connection<EventType, PropsType> getConnection() {
        return CONNECTION;
    }
    
    @Override
    public String getKey() {
        return KEY;
    }
    
    @Override
    public boolean isOptimized() {
        return true;
    }

    @Override
    public InputStream getImage(RequestMap map) {
        return CONNECTION.getImage(this, map);
    }

    @Override
    public int setImage(ByteArrayOutputStream os, RequestMap map) {
        return CONNECTION.setImage(this, os, map);
    }

    @Override
    public void getImage(final RequestMap map, ModelActionListener<InputStream> action) {
        callback(new CallbackAction<InputStream>(action) {

            @Override
            protected InputStream run() {
                return getImage(map);
            }
        });
    }
    
    @Override
    public PropsObj getProperties() {
        return createProperties(CONNECTION.getModel(this));
    }

    @Override
    public PropsObj getProperties(RequestMap map) {
        return createProperties(CONNECTION.getModel(this, map));
    }

    @Override
    public int askModel() {
        return CONNECTION.askModel(this);
    }

    @Override
    public int askModel(RequestMap map) {
        return CONNECTION.askModel(this, map);
    }

    @Override
    public int setProperty(RequestMap map) {
        return CONNECTION.setProperty(this, map);
    }

    @Override
    public void getProperties(ModelActionListener<PropsObj> action) {
        getProperties(null, action, null);
    }

    public void getProperties(ModelActionListener<PropsObj> action, ModelActionListener<PropsObj> action2) {
        getProperties(null, action, action2);
    }
    
    @Override
    public void getProperties(final RequestMap map, ModelActionListener<PropsObj> action) {
        getProperties(map, action, null);
    }
    
    public void getProperties(final RequestMap map, ModelActionListener<PropsObj> action, ModelActionListener<PropsObj> action2) {
        callback(new CallbackAction<PropsObj>(action, action2) {

            @Override
            protected PropsObj run() {
                return getProperties(map);
            }
            
        });
    }
    
    @Override
    public void askModel(ModelActionListener<Integer> action) {
        askModel(null, action, null);
    }
    
    public void askModel(ModelActionListener<Integer> action, ModelActionListener<Integer> action2) {
        askModel(null, action, action2);
    }
    
    @Override
    public void askModel(final RequestMap map, ModelActionListener<Integer> action) {
        askModel(map, action, null);
    }
    
    public void askModel(final RequestMap map, ModelActionListener<Integer> action, ModelActionListener<Integer> action2) {
        callback(new CallbackAction<Integer>(action, action2) {

            @Override
            protected Integer run() {
                return askModel(map);
            }
            
        });
    }
    
    @Override
    public void setProperty(final RequestMap map, ModelActionListener<Integer> action) {
        setProperty(map, action, null);
    }
    
    public void setProperty(final RequestMap map, ModelActionListener<Integer> action, ModelActionListener<Integer> action2) {
        callback(new CallbackAction<Integer>(action, action2) {

            @Override
            protected Integer run() {
                return setProperty(map);
            }
            
        });
    }
    
    @Override
    public boolean setListenerEnabled(boolean enable) {
        return setListenerEnabled(enable, true);
    }
    
    private boolean setListenerEnabled(boolean enable, boolean force) {
        if (!force && !isOptimized()) return false;
        if (enable) {
            if (!LISTENERS.isEmpty() && !isListening()) return CONNECTION.addListener(this);
        }
        else {
            if (isListening()) return CONNECTION.removeListener(this);
        }
        return false;
    }
    
    @Override
    public boolean isListening() {
        return CONNECTION.isListening(this);
    }
    
    @Override
    public List<ModelChangeListener<EventObj>> getListeners() {
        return LISTENERS;
    }
    
    @Override
    public boolean addListener(ModelChangeListener<EventObj> l) {
        synchronized(LISTENERS) {
            LISTENERS.add(l);
            fireStateListeners(l, ModelStateEvent.TYPE_ADD);
            if (LISTENERS.size() == 1 || !getConnection().isListening(this)) {
                if (!setListenerEnabled(true, true)) return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean removeListener(ModelChangeListener<EventObj> l) {
        synchronized(LISTENERS) {
            LISTENERS.remove(l);
            fireStateListeners(l, ModelStateEvent.TYPE_REMOVE);
            if (LISTENERS.isEmpty()) {
                if(!setListenerEnabled(false, false)) return false;
            }
        }
        return true;
    }
    
    public void removeListeners() {
        List<ModelChangeListener<EventObj>> ls = getListeners();
        for (ModelChangeListener<EventObj> l : ls) {
            removeListener(l);
        }
    }
    
    private void fireStateListeners(ModelChangeListener<EventObj> l, int type) {
        synchronized(STATE_LISTENERS) {
            ModelStateEvent e = new ModelStateEvent(this, l, type);
            for (ModelStateListener sl : STATE_LISTENERS) {
                sl.actionListenerChanged(e);
            }
        }
    }
    
    @Override
    public void addStateListener(ModelStateListener l) {
        synchronized(STATE_LISTENERS) {
            STATE_LISTENERS.add(l);
        }
    }
    
    @Override
    public void removeStateListener(ModelStateListener l) {
        synchronized(STATE_LISTENERS) {
            STATE_LISTENERS.remove(l);
        }
    }
    
    @Override
    public void fireModelChanged(List<EventType> es, int type, ConnectionException ex, boolean reset) {
        fireModelChanged(LISTENERS, es, type, ex, reset);
    }
    
    protected void fireModelChanged(List<ModelChangeListener<EventObj>> listeners, List<EventType> es, int type, ConnectionException ex, boolean reset) {
        synchronized(listeners) {
            if (es != null) {
                for (EventType e : es) {
                    ModelChangeEvent<EventObj> ev = new ModelChangeEvent<EventObj>(this, createEvent(e), reset);
                    for (ModelChangeListener<EventObj> l : listeners) {
                        l.fireModelChanged(ev);
                    }
                }
            }
            else {
                ModelChangeEvent<EventObj> ev;
                if (ex != null) ev = new ModelChangeEvent<EventObj>(this, ex, reset);
                else ev = new ModelChangeEvent<EventObj>(this, type, reset);
                for (ModelChangeListener<EventObj> l : listeners) {
                    l.fireModelChanged(ev);
                }
            }
        }
    }
    
    protected abstract EventObj createEvent(EventType e);
    
    protected abstract PropsObj createProperties(PropsType p);
    
    protected List<EventObj> createEventList(List<EventType> tl) {
        List<EventObj> ol = new ArrayList<EventObj>();
        for (EventType e : tl) {
            ol.add(createEvent(e));
        }
        return ol;
    }
    
    protected List<PropsObj> createPropertiesList(List<PropsType> tl) {
        List<PropsObj> ol = new ArrayList<PropsObj>();
        for (PropsType e : tl) {
            ol.add(createProperties(e));
        }
        return ol;
    }
    
    protected <T> void callback(final CallbackAction<T> action) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                ModelActionEvent<T> ev;
                try {
                    ev = new ModelActionEvent<T>(AbstractModel.this, action.run());
                }
                catch (ConnectionException ex) {
                    ev = new ModelActionEvent<T>(AbstractModel.this, ex);
                }
                catch (ControllerCloseException ex) {
                    ev = new ModelActionEvent<T>(AbstractModel.this, ex);
                }
                action.modelActionPerformed(ev);
            }
            
        }).start();
    }
        
}