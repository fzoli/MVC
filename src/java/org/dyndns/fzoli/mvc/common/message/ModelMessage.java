package org.dyndns.fzoli.mvc.common.message;

import java.util.Map;

/**
 *
 * @author zoli
 */
public class ModelMessage<PropsType> extends ServletMessage {
    
    private Map<String, PropsType> models;

    public static final String TYPE = "models";
    
    public ModelMessage(Map<String, PropsType> models) {
        super(TYPE);
        this.models = models;
    }

    public PropsType getProperty() {
        if (models.size() < 1) return null;
        return models.entrySet().iterator().next().getValue();
    }
    
    public PropsType getProperty(String model) {
        return models.get(model);
    }
    
    public Map<String, PropsType> getModels() {
        return models;
    }

    @Override
    public String toString() {
        return getModels().toString();
    }
    
}