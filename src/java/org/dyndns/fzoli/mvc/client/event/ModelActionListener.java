package org.dyndns.fzoli.mvc.client.event;

/**
 *
 * @author zoli
 */
public interface ModelActionListener<T> {
    
    void modelActionPerformed(ModelActionEvent<T> e);
    
}