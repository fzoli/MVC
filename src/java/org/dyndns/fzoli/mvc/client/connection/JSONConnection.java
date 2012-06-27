package org.dyndns.fzoli.mvc.client.connection;

import com.google.gson.Gson;
import java.util.Map;
import org.dyndns.fzoli.http.HttpExecutor;
import org.dyndns.fzoli.http.HttpUrl;
import org.dyndns.fzoli.mvc.common.message.*;
import org.dyndns.fzoli.mvc.common.message.map.ControllerCloseMap;
import org.dyndns.fzoli.mvc.common.message.map.ListenerCloseMap;

/**
 *
 * @author zoli
 */
public class JSONConnection extends Connection<Object, Object> {

    private static final Gson GSON = new Gson();

    public JSONConnection(HttpUrl url, HttpExecutor executor, String controllerServletName, String listenerServletName) {
        super(url, executor, controllerServletName, listenerServletName);
    }
    
    public JSONConnection(HttpUrl url, HttpExecutor executor, String projectName, String controllerServletName, String listenerServletName) {
        super(url, executor, projectName, controllerServletName, listenerServletName);
    }

    @Override
    protected String getResponseType(String response) {
        ServletMessage msg = GSON.fromJson(response, ServletMessage.class);
        return msg.getType();
    }
    
    @Override
    protected ControllerCloseMessage getControllerCloseMessage(String response) {
        ControllerCloseMap cm = new ControllerCloseMap(createCloseMap(response));
        return new ControllerCloseMessage(cm);
    }
    
    @Override
    protected ListenerCloseMessage getModelChangeListenerCloseMessage(String response) {
        ListenerCloseMap cm = new ListenerCloseMap(createCloseMap(response));
        return new ListenerCloseMessage(cm);
    }
    
    @Override
    protected ModelMessage<Object> getModelMessage(String response) {
        return GSON.fromJson(response, ModelMessage.class);
    }

    @Override
    protected ReturnMessage getReturnMessage(String response) {
        return GSON.fromJson(response, ReturnMessage.class);
    }

    @Override
    protected EventMessage<Object> getEventMessage(String response) {
        return GSON.fromJson(response, EventMessage.class);
    }
    
    private Map createCloseMap(String response) {
        Map m = GSON.fromJson(response, Map.class);
        return GSON.fromJson(GSON.toJsonTree(m.get("messages")), Map.class);
    }
    
}