package org.dyndns.fzoli.mvc.server.model.map;

import java.util.Map;
import org.dyndns.fzoli.mvc.common.map.CommonMap;
import org.dyndns.fzoli.mvc.server.model.Model;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;

/**
 *
 * @author zoli
 */
public abstract class ModelMap<ModelType extends Model> extends CommonMap<String, ModelType> {
    
    private ModelBean bean;
    
    public ModelMap() {
        super();
    }

    public ModelMap(Map<? extends String, ? extends ModelType> m) {
        super(m);
    }

    public ModelBean getModelBean() {
        return bean;
    }

    public void setModelBean(ModelBean bean) {
        this.bean = bean;
    }
    
    @Override
    public ModelType get(Object k) {
        return get(k, true);
    }

    public ModelType get(Object k, boolean init) {
        try {
            String key = (String) k;
            ModelType o = super.get(key);
            if (o == null && init) {
                ModelType m = init(key);
                if (m != null) put(key, m);
                return m;
            }
            return o;
        }
        catch (Exception ex) {
            return null;
        }
    }
    
    @Override
    public ModelType put(String key, ModelType value) {
        synchronized(this) {
            value.setLinks(this, key);
            return super.put(key, value);
        }
    }

    @Override
    public ModelType remove(Object key) {
        synchronized(this) {
            ModelType m = super.get(key);
            if (m != null) m.setLinks(null, null);
            return super.remove(key);
        }
    }
    
    public void onExpire() {
        ;
    }
    
    protected ModelType init(String key) {
        return null;
    }
    
}