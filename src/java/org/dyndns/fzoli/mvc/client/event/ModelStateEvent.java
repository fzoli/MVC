package org.dyndns.fzoli.mvc.client.event;

import org.dyndns.fzoli.mvc.client.event.type.ModelStateType;
import org.dyndns.fzoli.mvc.client.model.Model;

/**
 *
 * @author zoli
 */
public class ModelStateEvent implements CommonEvent<ModelChangeListener<?>>, ModelStateType {
    
    private int type;
    private Model<?,?,?,?> source;
    private ModelChangeListener<?> event;
    
    
    public ModelStateEvent(Model source, ModelChangeListener<?> event, int type) {
        this.source = source;
        this.event = event;
        this.type = type;
    }

    @Override
    public Model<?, ?, ?, ?> getSourceModel() {
        return source;
    }
    
    @Override
    public ModelChangeListener<?> getEvent() {
        return event;
    }

    @Override
    public int getType() {
        return type;
    }

}