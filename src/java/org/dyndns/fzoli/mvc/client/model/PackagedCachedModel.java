package org.dyndns.fzoli.mvc.client.model;

import java.util.List;
import org.dyndns.fzoli.mvc.client.event.ModelActionListener;

/**
 *
 * @author zoli
 */
public class PackagedCachedModel<EventType, PropsType, EventObj, PropsObj> extends CachedModel<EventType, PropsType, EventObj, PropsObj> {

    public PackagedCachedModel(CachedModel<EventType, PropsType, EventObj, PropsObj> model) {
        super(model);
    }

    public PackagedCachedModel(CachedModel<EventType, PropsType, EventObj, PropsObj> model, ModelActionListener<PropsObj> action) {
        super(model, action);
    }

    @Override
    protected CachedModel<EventType, PropsType, EventObj, PropsObj> getDefaultModel() {
        return getDefaultModel(CachedModel.class);
    }

    @Override
    protected void updateCache(List<EventObj> es, PropsObj cache) {
        getDefaultModel().updateCache(es, cache);
    }
    
}