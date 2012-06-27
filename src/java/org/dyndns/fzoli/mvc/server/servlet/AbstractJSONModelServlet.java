package org.dyndns.fzoli.mvc.server.servlet;

import com.google.gson.Gson;
import org.dyndns.fzoli.key.DataType;
import org.dyndns.fzoli.mvc.common.message.CloseMessage;

/**
 *
 * @author zoli
 */
public abstract class AbstractJSONModelServlet extends AbstractModelServlet<Object, Object> {

    private Gson gson = new Gson();

    protected Gson getGson() {
        return gson;
    }

    @Override
    protected String getContentType() {
        return DataType.JSON;
    }

    @Override
    public String closeMessageToString(CloseMessage msg) {
        return getGson().toJson(msg);
    }
    
}