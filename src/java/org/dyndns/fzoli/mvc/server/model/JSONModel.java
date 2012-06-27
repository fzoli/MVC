package org.dyndns.fzoli.mvc.server.model;

/**
 *
 * @author zoli
 */
public abstract class JSONModel<EventObj, PropsObj> extends AbstractModel<Object, Object, EventObj, PropsObj> {

    @Override
    protected final Object createProperties(PropsObj o) {
        return o;
    }

    @Override
    protected final Object createEvent(EventObj o) {
        return o;
    }
    
}