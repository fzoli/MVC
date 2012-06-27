package org.dyndns.fzoli.mvc.client.event;

import org.dyndns.fzoli.mvc.client.model.Model;

/**
 *
 * @author zoli
 */
interface CommonEvent<T> {
    
    int getType();
    
    Model<?, ?, ?, ?> getSourceModel();
    
    T getEvent();
    
}