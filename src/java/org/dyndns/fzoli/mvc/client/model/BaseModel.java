package org.dyndns.fzoli.mvc.client.model;

import java.util.List;
import org.dyndns.fzoli.mvc.client.connection.Connection;
import org.dyndns.fzoli.mvc.client.connection.exception.ConnectionException;

/**
 *
 * @author zoli
 */
public interface BaseModel<EventType, PropsType> {
    
    String getKey();
    
    Connection<EventType, PropsType> getConnection();
    
    void fireModelChanged(List<EventType> e, int type, ConnectionException ex, boolean reset);
    
}