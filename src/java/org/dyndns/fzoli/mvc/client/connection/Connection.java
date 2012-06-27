package org.dyndns.fzoli.mvc.client.connection;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.dyndns.fzoli.http.HttpExecutor;
import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.http.data.HttpResponseReturn;
import org.dyndns.fzoli.http.data.HttpStreamReturn;
import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;
import org.dyndns.fzoli.mvc.client.connection.exception.ControllerCloseException;
import org.dyndns.fzoli.mvc.client.event.type.ModelActionEventType;
import org.dyndns.fzoli.mvc.client.event.type.ModelChangeEventType;
import org.dyndns.fzoli.mvc.client.model.BaseModel;
import org.dyndns.fzoli.mvc.common.message.*;
import org.dyndns.fzoli.mvc.common.message.map.ListenerCloseMap;
import org.dyndns.fzoli.mvc.common.request.map.ControllerServletRequestMap;
import org.dyndns.fzoli.mvc.common.request.map.RequestMap;
import org.dyndns.fzoli.mvc.common.request.map.ServletRequestMap;

/**
 *
 * @author zoli
 */
public abstract class Connection<EventType, PropsType> implements ModelChangeEventType, ModelActionEventType {
    
    private final int MODE_FIRST_START = -1, MODE_NORMAL = 0, MODE_TIMEOUT = -2;
    
    private boolean validId = false;
    private String listenerId = null;
    private int errorCounter = 0, retryCount = 0, plusTimeout = 5000;
    private long serverInitTime = 0, timeoutTime;
    
    private boolean logEnabled = true, closed = false;
    private int timeoutParameter, stopTimeout = 30000;
    private Log log = LogFactory.getLog(Connection.class);
    
    private final HttpExecutor EXECUTOR;
    private final HttpUrl CONTROLLER_URL, LISTENER_URL;
    private final List<BaseModel<EventType, PropsType>> MODELS = new ArrayList<BaseModel<EventType, PropsType>>();
    
    public Connection(HttpUrl url, HttpExecutor executor, String controllerServletName, String listenerServletName) {
        this(url, executor, null, controllerServletName, listenerServletName);
    }

    public Connection(HttpUrl url, HttpExecutor executor, String projectName, String controllerServletName, String listenerServletName) {
        this.CONTROLLER_URL = createUrl(url, projectName, controllerServletName);
        this.LISTENER_URL = createUrl(url, projectName, listenerServletName);
        this.EXECUTOR = executor;
        this.timeoutParameter = executor.getConnectionTimeout();
    }
    
    // <editor-fold defaultstate="collapsed" desc="Common methods.">
    
    public void close() {
        closed = true;
        interruptWaitEvent();
        getHttpExecutor().abortAll();
    }
    
    public void setLogEnabled(boolean enabled) {
        logEnabled = enabled;
    }
    
    public HttpExecutor getHttpExecutor() {
        return EXECUTOR;
    }
    
    public void setRetryCount(int count) {
        retryCount = count;
    }
    
    public int getStopTimeout() {
        return stopTimeout;
    }
    
    public void setStopTimeout(int stopTimeout) {
        this.stopTimeout = stopTimeout < 0 ? 0 : stopTimeout;
    }

    public int getPlusTimeout() {
        return plusTimeout;
    }

    public void setPlusTimeout(int plusTimeout) {
        this.plusTimeout = plusTimeout < 0 ? 0 : plusTimeout;
    }
    
    public int getServerEventTimeout() {
        return timeoutParameter;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Controller methods.">
    // <editor-fold defaultstate="collapsed" desc="getModel()">
    public PropsType getModel(BaseModel<EventType, PropsType> model) {
        return getModel(model, null);
    }
    
    public ModelMessage<PropsType> getModel(List<BaseModel<EventType, PropsType>> models) {
        return getModel(models, null);
    }
    
    public PropsType getModel(BaseModel<EventType, PropsType> model, RequestMap map) {
        return getModel(createModelList(model), map).getProperty(model.getKey());
    }
    
    public ModelMessage<PropsType> getModel(List<BaseModel<EventType, PropsType>> models, RequestMap map) {
        String action = ControllerServletRequestMap.ACTION_GET_MODEL;
        String response = controllerExecute(createControllerServletRequestMap(action, models, map));
        return getModelMessage(response);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="getImage()">
    public InputStream getImage(BaseModel<EventType, PropsType> model, RequestMap map) {
        try {
            HttpStreamReturn ret = EXECUTOR.execute(CONTROLLER_URL, createControllerServletRequestMap(ControllerServletRequestMap.ACTION_GET_IMAGE, createModelList(model), map), 60000);
            return ret.getStream();
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    public int setImage(BaseModel<EventType, PropsType> model, ByteArrayOutputStream bos, RequestMap map) {
        ControllerServletRequestMap m = createControllerServletRequestMap(ControllerServletRequestMap.ACTION_SET_IMAGE, createModelList(model), map);
        MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        if (bos != null) reqEntity.addPart(ControllerServletRequestMap.KEY_IMG, new ByteArrayBody(bos.toByteArray(), ControllerServletRequestMap.KEY_IMG));
        return Integer.parseInt(getReturnMessage(controllerExecute(m, reqEntity)).getValue());
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="askModel()">
    
    public int askModel(BaseModel<EventType, PropsType> model) {
        return setAskModel(model, null, true);
    }
    
    public int askModel(BaseModel<EventType, PropsType> model, RequestMap map) {
        return setAskModel(model, map, true);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="setModel()">
    public int setProperty(BaseModel<EventType, PropsType> model, RequestMap map) {
        return setAskModel(model, map, false);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="manageListeners()">
    public boolean addListener(BaseModel<EventType, PropsType> model) {
        return addListener(createModelList(model));
    }
    
    public boolean addListener(List<BaseModel<EventType, PropsType>> models) {
        return manageListeners(models, true);
    }
    
    public boolean removeListener(BaseModel<EventType, PropsType> model) {
        return removeListener(createModelList(model));
    }
    
    public boolean removeListener(List<BaseModel<EventType, PropsType>> models) {
        return manageListeners(models, false);
    }
    
    public boolean isListening(BaseModel<EventType, PropsType> model) {
        synchronized(MODELS) {
            return MODELS.indexOf(model) != -1;
        }
    }
    
    public boolean isWaitEvent() {
        return waiterThread == null ? false : waiterThread.isAlive();
    }
    
    public void waitEvent() {
        //log("Wait event request");
        closed = false;
        if (!isWaitEvent() && !MODELS.isEmpty()) {
            (waiterThread = new Thread(WAITER)).start();
        }
    }
    
    public void interruptWaitEvent() {
        //log("Wait event interrupt request");
        if (waiterThread != null) {
            log("Wait event interrupt");
            waiterThread.interrupt();
        }
    }
    // </editor-fold>
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Abstract common methods.">
    protected abstract String getResponseType(String response);
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Abstract Controller methods.">
    protected abstract ControllerCloseMessage getControllerCloseMessage(String response);
    
    protected abstract ModelMessage<PropsType> getModelMessage(String response);
    
    protected abstract ReturnMessage getReturnMessage(String response);
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Abstract ModelChangeListener methods.">
    protected abstract ListenerCloseMessage getModelChangeListenerCloseMessage(String response);
    
    protected abstract EventMessage<EventType> getEventMessage(String response);
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Private methods.">
    private boolean isCloseMessage(String response) {
        return getResponseType(response).equals(CloseMessage.TYPE);
    }
    
    private List<BaseModel<EventType, PropsType>> getModel(String name) {
        List<BaseModel<EventType, PropsType>> l = new ArrayList<BaseModel<EventType, PropsType>>();
        for (BaseModel<EventType, PropsType> m : MODELS) {
            if (m.getKey().equals(name)) l.add(m);
        }
        return l;
    }
    
    private void log(Object o) {
        log(o, null);
    }
    
    private void log(Object o, Exception e) {
        if (log.isInfoEnabled() && logEnabled) log.info(o, e);
    }
    
    private String execute(HttpUrl url, Map<String, List<String>> map, Integer timeout) {
        return execute(url, map, null, timeout);
    }
    
    private String execute(HttpUrl url, Map<String, List<String>> map, HttpEntity entity, Integer timeout) {
        HttpResponseReturn ret = EXECUTOR.getResponse(url, map, entity, timeout);
        if (!ret.isStatusOk()) {
            errorCounter++;
            if (errorCounter >= retryCount) {
                errorCounter = 0;
                throw new ConnectionException(ret);
            }
            else {
                sleep();
                return execute(url, map, entity, timeout);
            }
        }
        return ret.getResponse();
    }
    
    private String listenerExecute() {
        final ServletRequestMap map = createServletRequestMap();
        final Timer t = new Timer(), it = new Timer();
        t.schedule(new TimerTask() {

            @Override
            public void run() {
                if (timeoutTime == MODE_FIRST_START) {
                    log("Immediately disconnect");
                    EXECUTOR.abort(LISTENER_URL, map);
                }
                else if (timeoutTime == MODE_NORMAL) {
                    log("Disconnect detected");
                    fireModels(TYPE_SERVER_LOST, null, false);
                    timeoutTime = MODE_TIMEOUT;
                    it.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            EXECUTOR.abort(LISTENER_URL, map);
                        }
                        
                    }, getStopTimeout());
                }
            }

        }, getServerEventTimeout() + getPlusTimeout());
        String s;
        try {
            s = execute(LISTENER_URL, map, null);
            it.cancel();
            t.cancel();
        }
        catch (RuntimeException ex) {
            it.cancel();
            t.cancel();
            throw ex;
        }
        return s;
    }
    
    private String controllerExecute(Map<String, List<String>> map) {
        return controllerExecute(map, null);
    }
    
    private String controllerExecute(Map<String, List<String>> map, HttpEntity entity) {
        String response = execute(entity == null ? CONTROLLER_URL : new HttpUrl(CONTROLLER_URL, map), map, entity, EXECUTOR.getConnectionTimeout() + getPlusTimeout());
        checkControllerResponse(response);
        return response;
    }
    
    private void checkControllerResponse(String response) {
        if (isCloseMessage(response)) throw new ControllerCloseException(getControllerCloseMessage(response));
    }
    
    private int setAskModel(BaseModel<EventType, PropsType> model, RequestMap map, boolean ask) {
        String action = ask ? ControllerServletRequestMap.ACTION_ASK_MODEL : ControllerServletRequestMap.ACTION_SET_MODEL;
        String response = controllerExecute(createControllerServletRequestMap(action, createModelList(model), map));
        return Integer.parseInt(getReturnMessage(response).getValue());
    }
    
    private final ControllerServletRequestMap TEST_REQUEST_MAP = new ControllerServletRequestMap(ControllerServletRequestMap.ACTION_TEST);
    
    private Thread waiterThread;
    
    private void sleep() {
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException ex) {
            ;
        }
    }
    
    private final Runnable WAITER = new Runnable() {

        @Override
        public void run() {
            log("Wait event started");
            serverInitTime = 0;
            timeoutTime = MODE_FIRST_START;
            while(!closed && !MODELS.isEmpty()) {
                boolean tested = false;
                String response;
                try {
                    if (timeoutTime != MODE_NORMAL) {
                        try {
                            controllerExecute(TEST_REQUEST_MAP);
                            if (timeoutTime == MODE_FIRST_START) timeoutTime = MODE_NORMAL;
                        }
                        catch (ControllerCloseException ex) {
                            if (ex.getReason().equals(ControllerCloseMessage.REASON_TEST)) {
                                tested = true;
                                log("Connection test OK");
                                if (timeoutTime != MODE_FIRST_START) {
                                    log("Reconnected");
                                    fireModels(TYPE_SERVER_RECONNECT, null, false);
                                }
                                timeoutTime = MODE_NORMAL;
                            }
                        }
                    }
                    response = listenerExecute();
                }
                catch (ConnectionException ex) {
                    if (!tested && timeoutTime == MODE_NORMAL) {
                        timeoutTime = new Date().getTime() + getStopTimeout();
                        log("Server was lost", ex);
                        fireModels(TYPE_SERVER_LOST, null, false);
                        sleep();
                        continue;
                    }
                    else {
                        if (tested || timeoutTime == MODE_FIRST_START || new Date().getTime() >= timeoutTime) {
                            log("Listener exception", ex);
                            fireModels(TYPE_CONNECTION_EXCEPTION, ex, true);
                            break;
                        }
                        else {
                            sleep();
                            continue;
                        }
                    }
                }
                if (isCloseMessage(response)) {
                    ListenerCloseMap msg = getModelChangeListenerCloseMessage(response).getMessages();
                    timeoutParameter = msg.getTimeoutParameter();
                    if (msg.isNewId()) {
                        listenerId = msg.getListenerId();
                        if (serverInitTime != 0 && serverInitTime != msg.getInitTime()) {
                            timeoutTime = MODE_NORMAL;
                            log("Server was restarted");
                            fireModels(TYPE_SERVER_RECONNECT, null, false);
                        }
                        serverInitTime = msg.getInitTime();
                        validId = true;
                        log("New Listener id: " + listenerId);
                        try {
                            manageListenersExecute(MODELS, true);
                        }
                        catch (ConnectionException ex) {
                            log("Manage listener connection exception", ex);
                            sleep();
                            continue;
                        }
                        catch (ControllerCloseException ex) {
                            log("Manage listener controller exception", ex);
                            break;
                        }
                    }
                    else {
                        String reason = msg.getReason();
                        if (reason.equals(ListenerCloseMessage.REASON_KICKED)) {
                            listenerId = null;
                            log("Listener id kicked");
                        }
                        else if (reason.equals(ListenerCloseMessage.REASON_NO_LISTENERS)) {
                            validId = false;
                            log("No listeners");
                            break;
                        }
                    }
                    
                }
                else {
                    Map<String, List<EventType>> evts = getEventMessage(response).getEvents();
                    log("GOT EventMessage: " + evts.keySet());
                    Iterator<Entry<String, List<EventType>>> it = evts.entrySet().iterator();
                    while (it.hasNext()) {
                        Entry<String, List<EventType>> e = it.next();
                        List<BaseModel<EventType, PropsType>> models = getModel(e.getKey());
                        for (BaseModel<EventType, PropsType> model : models) {
                            model.fireModelChanged(e.getValue(), TYPE_EVENT, null, false);
                        }
                    }
                }
            }
            log("Wait event stopped");
            waiterThread = null;
        }

    };

    private void fireModels(int type, ConnectionException ex, boolean reset) {
        List<BaseModel<EventType, PropsType>> tmp;
        if (reset) {
            tmp = new ArrayList<BaseModel<EventType, PropsType>>(MODELS);
            MODELS.clear();
        }
        else {
            tmp = MODELS;
        }
        for (BaseModel<EventType, PropsType> model : tmp) {
            model.fireModelChanged(null, type, ex, reset);
        }
    }
    
    private boolean manageListenersExecute(List<BaseModel<EventType, PropsType>> models, boolean enable) {
        if (validId && !models.isEmpty()) {
            String action = enable ? ControllerServletRequestMap.ACTION_REG_LISTENER : ControllerServletRequestMap.ACTION_UNREG_LISTENER;
            List<String> modelNames = createModelKeyList(models);
            try {
                controllerExecute(new ControllerServletRequestMap(action, listenerId, modelNames));
            }
            catch (ConnectionException ex) {
                return false;
            }
            catch (ControllerCloseException ex) {
                if (ex.getReason().equals(ControllerCloseException.REASON_INVALID_LISTENER_ID)) {
                    log("Reset listener ID", ex);
                    listenerId = null;
                }
                else {
                    throw ex;
                }
            }
        }
        return true;
    }
    
    private boolean manageListeners(List<BaseModel<EventType, PropsType>> models, boolean enable) {
        boolean success = true;
        List<BaseModel<EventType, PropsType>> filteredModels = new ArrayList<BaseModel<EventType, PropsType>>();
        for (BaseModel<EventType, PropsType> m : models) {
            if (enable) {
                if (!isListening(m)) filteredModels.add(m);
            }
            else {
                if (isListening(m)) filteredModels.add(m);
            }
        }
        if (isWaitEvent()) {
            if (!validId) {
                while(isWaitEvent()) {
                    if (validId) break;
                }
            }
            success = manageListenersExecute(filteredModels, enable);
        }
        synchronized(MODELS) {
            if (enable) MODELS.addAll(filteredModels);
            else MODELS.removeAll(filteredModels);
        }
        if (enable) {
            waitEvent();
        }
        return success;
    }
    
    private ServletRequestMap createServletRequestMap() {
        if (listenerId == null) return new ServletRequestMap();
        else return new ServletRequestMap(listenerId);
    }
    
    private ControllerServletRequestMap createControllerServletRequestMap(String action, List<BaseModel<EventType, PropsType>> models, RequestMap map) {
        ControllerServletRequestMap cm = new ControllerServletRequestMap(action, createModelKeyList(models));
        if (map != null) cm.putAll(map);
        return cm;
    }
    
    private List<String> createModelKeyList(List<BaseModel<EventType, PropsType>> models) {
        List<String> l = new ArrayList<String>();
        for (BaseModel m : models) {
            l.add(m.getKey());
        }
        return l;
    }
    
    private List<BaseModel<EventType, PropsType>> createModelList(final BaseModel<EventType, PropsType> val) {
        return new ArrayList<BaseModel<EventType, PropsType>>(){{add(val);}};
    }
    
    private HttpUrl createUrl(HttpUrl url, String projectName, String servletName) {
        if (url == null) throw new NullPointerException("HTTP URL can not be null");
        if (servletName == null) throw new NullPointerException("Servlet name can not be null");
        String path = (projectName == null ? "" : projectName + "/")  + servletName;
        HttpUrl hurl = new HttpUrl(url);
        hurl.setPath(path);
        return hurl;
    }
    // </editor-fold>
    
}