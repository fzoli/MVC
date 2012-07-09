package org.dyndns.fzoli.mvc.common.request.map;

import java.util.List;
import java.util.Map;

/**
 *
 * @author zoli
 */
public class ControllerServletRequestMap extends ServletRequestMap {

    private static final String ACTION = "action";
    private static final String MODEL = "model";
    
    public static final String KEY_IMG = "img";
    
    public static final String ACTION_TEST = "test";
    public static final String ACTION_GET_IMAGE = "get_image";
    public static final String ACTION_SET_IMAGE = "set_image";
    public static final String ACTION_ASK_MODEL = "ask_model";
    public static final String ACTION_GET_MODEL = "get_model";
    public static final String ACTION_SET_MODEL = "set_model";
    public static final String ACTION_REG_LISTENER = "reg_listener";
    public static final String ACTION_UNREG_LISTENER = "unreg_listener";
    
    public ControllerServletRequestMap(Map<String, String[]> m) {
        super(m);
        initValue(ACTION);
        initValue(MODEL);
    }

    public ControllerServletRequestMap(String action) {
        super();
        setAction(action);
    }
    
    public ControllerServletRequestMap(String action, List<String> models) {
        super();
        setAction(action);
        setModels(models);
    }
    
    public ControllerServletRequestMap(String action, String model) {
        super();
        setAction(action);
        setModel(model);
    }
    
    public ControllerServletRequestMap(String action, List<String> models, Map<? extends String, ? extends List<String>> attrs) {
        this(action, models);
        putAll(attrs);
    }
    
    public ControllerServletRequestMap(String action, String model, Map<? extends String, ? extends List<String>> attrs) {
        this(action, model);
        putAll(attrs);
    }
    
    public ControllerServletRequestMap(String action, String listenerId, List<String> models) {
        super(listenerId);
        setAction(action);
        setModels(models);
    }
    
    public ControllerServletRequestMap(String action, String listenerId, String model) {
        super(listenerId);
        setAction(action);
        setModel(model);
    }
    
    public final String getAction() {
        return getValue(ACTION);
    }

    private void setAction(String action) {
        setValue(ACTION, action);
    }

    private void setModel(String model) {
        setValue(MODEL, model);
    }
    
    public final List<String> getModels() {
        return getValues(MODEL);
    }

    private void setModels(List<String> models) {
        setValues(MODEL, models);
    }

    @Override
    public void putAll(Map<? extends String, ? extends List<String>> m) {
        if (m.containsKey(ACTION)) throw new IllegalArgumentException(ACTION + RESERVED_KEY);
        if (m.containsKey(MODEL)) throw new IllegalArgumentException(MODEL + RESERVED_KEY);
        super.putAll(m);
    }
    
}