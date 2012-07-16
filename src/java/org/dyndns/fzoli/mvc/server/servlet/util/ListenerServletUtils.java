package org.dyndns.fzoli.mvc.server.servlet.util;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.bitwalker.useragentutils.RenderingEngine;
import org.apache.commons.codec.digest.DigestUtils;
import org.dyndns.fzoli.mvc.common.message.EventMessage;
import org.dyndns.fzoli.mvc.common.message.ListenerCloseMessage;
import org.dyndns.fzoli.mvc.common.request.map.ServletRequestMap;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.servlet.listener.ListenerIDRegister;
import org.dyndns.fzoli.mvc.server.servlet.listener.ListenerServlet;

/**
 *
 * @author zoli
 */
public class ListenerServletUtils<EventType, PropsType> extends ServletUtils<EventType, PropsType> {
    
    private final Date START_DATE = new Date();
    private final ListenerIDRegister ID_REGISTER;

    public ListenerServletUtils(ListenerServlet<EventType, PropsType> servlet) {
        super(servlet);
        this.ID_REGISTER = new ListenerIDRegister(getReconnectWait(), getGCDelay());
    }

    @Override
    protected ListenerServlet<EventType, PropsType> getServlet() {
        return (ListenerServlet<EventType, PropsType>) super.getServlet();
    }

    private ListenerIDRegister getIdRegister() {
        return ID_REGISTER;
    }

    private long getInitTime() {
        return START_DATE.getTime();
    }
    
    private String getSessionId(HttpServletRequest request) {
        return request.getSession(true).getId();
    }
    
    private String createListenerId(HttpServletRequest request) {
        String sessid = getSessionId(request);
        double salt = new Date().getTime() + Math.random();
        String gen = DigestUtils.md5Hex(sessid + salt);
        getIdRegister().updateRegTime(gen);
        getIdRegister().setSessionId(gen, sessid);
        ListenerIDRegister.addListenerId(gen);
        return gen;
    }
    
    private boolean isTimeout(Date start, int timeout) {
        return timeout != 0 && new Date().getTime() - start.getTime() >= timeout;
    }
    
    private boolean isSessionIdValid(String listenerId, HttpServletRequest request) {
        return getIdRegister().isSessionIdEquals(listenerId, getSessionId(request));
    }
    
    private boolean isListenerServerKicked(HttpServletRequest request) {
        return !getIdRegister().isListenerServletRegistrated(request);
    }
    
    private void printCloseMessage(String listenerId, boolean newId, int timeoutParam, String reason, HttpServletResponse response) {
        String ret = getServlet().closeMessageToString(new ListenerCloseMessage(reason, listenerId, getInitTime(), timeoutParam, newId));
        response.setContentLength(ret.getBytes().length);
        printString(response, ret);
    }
    
    private void printEventMessage(HttpServletResponse response, EventMessage<EventType> msg) {
        String ret = getServlet().eventMessageToString(msg);
        response.setContentLength(ret.getBytes().length);
        printString(response, ret);
    }
    
    private void kickServletsWithSameId(String listenerId, HttpServletRequest request) {
        List<HttpServletRequest> reqs = getIdRegister().getRequestsWithListenerId(listenerId);
        for (HttpServletRequest req : reqs) {
            if (!req.equals(request)) getIdRegister().unregisterListenerId(req);
        }
    }
    
    private String createCloseReason(boolean isTimeout, boolean isKicked) {
        return isTimeout ? ListenerCloseMessage.REASON_TIMEOUT : isKicked ? ListenerCloseMessage.REASON_KICKED : ListenerCloseMessage.REASON_NO_LISTENERS;
    }
    
    private int getRequestCounter(String id, ModelBean bean) {
        boolean b = bean.isListenerRegistrated(id);
        int c = getIdRegister().getRequestCounter(id);
        if (b) getIdRegister().increaseRequestCounter(id);
        return c;
    }
    
    public int getEventDelay() {
        return getConfigNumber(ListenerServlet.PARAM_EVENT_DELAY, 1, 1000, 10);
    }
    
    public int getEventTimeout() {
        return getConfigNumber(ListenerServlet.PARAM_EVENT_TIMEOUT, 10000, 1800000, 10000);
    }

    public int getReconnectWait() {
        return getConfigNumber(ListenerServlet.PARAM_RECONNECT_WAIT, 0, 30000);
    }

    private int getGCDelay() {
        return getConfigNumber(ListenerServlet.PARAM_GC_DELAY, 60000, 600000);
    }
    
    private int getConfigNumber(String name, int min, int max) {
        return getConfigNumber(name, min, max, min);
    }
    
    private EventMessage<EventType> getEvents(HttpServletRequest request, String listenerId) {
        return getServlet().getModelBean(request).getEvents(listenerId);
    }
    
    private void sleep(int delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException ex) {}
    }

    @Override
    public ServletRequestMap createRequestMap(HttpServletRequest request) {
        return new ServletRequestMap(request.getParameterMap());
    }
    
    @Override
    public void printResponse(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean newId;
        int timeout = getEventTimeout();
        String listenerId = createRequestMap(request).getListenerId();
        if (isListenerIdExists(listenerId) && isSessionIdValid(listenerId, request)) {
            newId = false;
            kickServletsWithSameId(listenerId, request);
        }
        else {
            newId = true;
            listenerId = createListenerId(request);
        }
        if (!newId) {
            getIdRegister().registerListenerId(request, listenerId);
            boolean isTimeout = false;
            boolean isKicked = false;
            int delay = getEventDelay();
            ModelBean<EventType, PropsType> models = getServlet().getModelBean(request);
            int counter = getRequestCounter(listenerId, models);
            RenderingEngine engine = getServlet().getUserAgent(request).getBrowser().getRenderingEngine();
            if (counter == 0 && engine == RenderingEngine.WEBKIT) timeout = 1000; //trick for remove chrome, safari, android and other webkit browsers indicator
            Date start = new Date();
            while (models.isListenerRegistrated(listenerId) && !models.isEvent(listenerId)) {
                if (isTimeout(start, timeout)) {
                    isTimeout = true;
                    break;
                }
                if (isListenerServerKicked(request)) {
                    isKicked = true;
                    break;
                }
                sleep(delay);
            }
            if (models.isEvent(listenerId)) printEventMessage(response, getEvents(request, listenerId));
            else printCloseMessage(listenerId, newId, timeout, createCloseReason(isTimeout, isKicked), response);
            getIdRegister().unregisterListenerId(request);
        }
        else {
            printCloseMessage(listenerId, newId, timeout, ListenerCloseMessage.REASON_NEW_ID, response);
        }
    }
    
}