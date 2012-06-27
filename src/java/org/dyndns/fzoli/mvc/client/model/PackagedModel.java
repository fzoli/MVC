package org.dyndns.fzoli.mvc.client.model;

/**
 *
 * @author zoli
 */
public class PackagedModel<EventType, PropsType, EventObj, PropsObj> extends AbstractModel<EventType, PropsType, EventObj, PropsObj> {

    private final AbstractModel<EventType, PropsType, EventObj, PropsObj> MODEL;
    
    public PackagedModel(AbstractModel<EventType, PropsType, EventObj, PropsObj> model) {
        super(model);
        MODEL = model;
    }

    protected AbstractModel<EventType, PropsType, EventObj, PropsObj> getDefaultModel() {
        return getDefaultModel(AbstractModel.class);
    }
    
    protected <T extends AbstractModel<EventType, PropsType, EventObj, PropsObj>> T getDefaultModel(Class<T> clazz) {
        return (T) MODEL;
    }
    
    @Override
    protected EventObj createEvent(EventType e) {
        return getDefaultModel().createEvent(e);
    }

    @Override
    protected PropsObj createProperties(PropsType p) {
        return getDefaultModel().createProperties(p);
    }
    
}