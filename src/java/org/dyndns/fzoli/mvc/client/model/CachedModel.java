package org.dyndns.fzoli.mvc.client.model;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.event.*;

/**
 *
 * @author zoli
 */
public abstract class CachedModel<EventType, PropsType, EventObj, PropsObj> extends PackagedModel<EventType, PropsType, EventObj, PropsObj> {

    private PropsObj cache;
    private ConnectionException eventEx;
    private RuntimeException propsEx;
    private ModelActionListener<PropsObj> action;
    private boolean outdated = false;
    
    protected boolean DEFAULT_WAIT_INIT_CACHE = false, DEFAULT_SEE_EVENT_EX = false, DEFAULT_RETRY = true;
    
    public CachedModel(AbstractModel<EventType, PropsType, EventObj, PropsObj> model) {
        this(model, null);
    }
    
    public CachedModel(AbstractModel<EventType, PropsType, EventObj, PropsObj> model, ModelActionListener<PropsObj> action) {
        super(model);
        this.action = action;
        initCache(true);
    }

    protected RuntimeException getPropsException() {
        return propsEx;
    }

    protected ConnectionException getEventException() {
        return eventEx;
    }
    
    @Override
    public boolean isOptimized() {
        return false;
    }
    
    public boolean isCacheOutdated() {
        return outdated;
    }
    
    public boolean isCacheNull() {
        return isCacheNull(DEFAULT_WAIT_INIT_CACHE);
    }
    
    public boolean isCacheNull(boolean waitInitCache) {
        if (waitInitCache) waitInitCache();
        return cache == null;
    }
    
    public boolean isCacheSafe() {
        return isCacheSafe(DEFAULT_WAIT_INIT_CACHE);
    }
    
    public boolean isCacheSafe(boolean waitInitCache) {
        return isCacheSafe(waitInitCache, DEFAULT_SEE_EVENT_EX);
    }
    
    public boolean isCacheSafe(boolean waitInitCache, boolean seeEventEx) {
        return !isCacheNull(waitInitCache) && propsEx != null && (seeEventEx ? eventEx == null : true);
    }
    
    @Override
    public void fireModelChanged(List<EventType> es, int type, ConnectionException ex, boolean reset) {
        switch (type) {
            case ModelChangeEvent.TYPE_SERVER_LOST:
                outdated = true;
                super.fireModelChanged(es, type, ex, reset);
                break;
            case ModelChangeEvent.TYPE_SERVER_RECONNECT:
                outdated = false;
                setListenerEnabled(true);
                reset = false;
                reinitCache(type, false);
                if (propsEx != null) break;
            case ModelChangeEvent.TYPE_CONNECTION_EXCEPTION:
                eventEx = ex;
            default:
                if (es != null) {
                    eventEx = null;
                    outdated = false;
                    if (!es.isEmpty()) updateCache(createEventList(es), cache);
                }
                super.fireModelChanged(es, type, ex, reset);
        }
    }
    
    private void fireCacheReload(int type) {
        List<ModelChangeListener<EventObj>> listeners = getListeners();
        synchronized(listeners) {
            for (ModelChangeListener<EventObj> listener : listeners) {
                if (listener instanceof CachedModelChangeListener) ((CachedModelChangeListener)listener).fireCacheReload(type);
            }
        }
    }
    
    public PropsObj getCache() {
        return getCache(DEFAULT_RETRY);
    }
    
    public PropsObj getCache(boolean retry) {
        return getCache(retry, DEFAULT_SEE_EVENT_EX);
    }
    
    public PropsObj getCache(boolean retry, boolean throwEventEx) {
        if (throwEventEx && eventEx != null) throw eventEx;
        waitInitCache();
        if (willCacheReinit(retry)) {
            reinitCache(ModelChangeEvent.TYPE_CONNECTION_EXCEPTION, true);
        }
        if (cache == null && propsEx != null) throw propsEx;
        return cache;
    }
    
    public void getCache(ModelActionListener<PropsObj> action) {
        getCache(action, DEFAULT_RETRY);
    }
    
    public void getCache(ModelActionListener<PropsObj> action, final boolean retry) {
        getCache(action, retry, DEFAULT_SEE_EVENT_EX);
    }
    
    public void getCache(ModelActionListener<PropsObj> action, final boolean retry, final boolean throwEventEx) {
        callback(new CallbackAction<PropsObj>(action) {

            @Override
            protected PropsObj run() {
                return getCache(retry, throwEventEx);
            }
            
        });
    }
    
    public boolean willCacheReinit() {
        return willCacheReinit(DEFAULT_RETRY);
    }
    
    public boolean willCacheReinit(boolean retry) {
        return retry && (eventEx != null || (propsEx != null && propsEx instanceof ConnectionException));
    }
    
    public void reinitCache() {
        reinitCache(0, false);
    }
    
    protected abstract void updateCache(List<EventObj> es, PropsObj cache);
    
    private void waitInitCache() {
        while (isCacheInitializing()) {
            try {
                Thread.sleep(1);
            }
            catch (InterruptedException ex) {
                ;
            }
        }
    }
    
    private void reinitCache(int type, boolean fire) {
        if (fire) fireCacheReload(type);
        initCache(false);
        waitInitCache();
    }
    
    public boolean isCacheInitializing() {
        return cache == null && propsEx == null;
    }
    
    private void initCache(boolean fire) {
        if (!fire && isCacheInitializing()) return;
        cache = null;
        outdated = false;
        propsEx = eventEx = null;
        final boolean isAction = fire && action != null;
        getProperties(new ModelActionListener<PropsObj>() {

                @Override
                public void modelActionPerformed(ModelActionEvent<PropsObj> e) {
                    switch (e.getType()) {
                        case ModelActionEvent.TYPE_EVENT:
                            cache = e.getEvent();
                            break;
                        case ModelActionEvent.TYPE_CONNECTION_EXCEPTION:
                            propsEx = e.getConnectionException();
                            break;
                        case ModelActionEvent.TYPE_CONTROLLER_CLOSE_EXCEPTION:
                            propsEx = e.getControllerCloseException();
                            break;
                    }
                    if (isAction) action.modelActionPerformed(e);
                }

        });
    }
    
}