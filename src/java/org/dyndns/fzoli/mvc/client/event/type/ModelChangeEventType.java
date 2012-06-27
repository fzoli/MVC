package org.dyndns.fzoli.mvc.client.event.type;

/**
 *
 * @author zoli
 */
public interface ModelChangeEventType extends ModelEventType {
    
    int TYPE_SERVER_RECONNECT = 1;
    
    int TYPE_SERVER_LOST = 2;
    
}