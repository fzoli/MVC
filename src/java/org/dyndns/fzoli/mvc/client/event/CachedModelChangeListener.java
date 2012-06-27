package org.dyndns.fzoli.mvc.client.event;

/**
 *
 * @author zoli
 */
public interface CachedModelChangeListener<T> extends ModelChangeListener<T> {
    
    void fireCacheReload(int type);
    
}