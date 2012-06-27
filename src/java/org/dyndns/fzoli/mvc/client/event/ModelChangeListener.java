package org.dyndns.fzoli.mvc.client.event;

/**
 *
 * @author zoli
 */
public interface ModelChangeListener<T> {
    
    void fireModelChanged(ModelChangeEvent<T> e);
    
}