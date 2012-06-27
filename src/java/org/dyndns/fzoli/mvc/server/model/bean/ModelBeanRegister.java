package org.dyndns.fzoli.mvc.server.model.bean;

import java.util.List;
import org.dyndns.fzoli.bean.BeanRegister;

/**
 *
 * @author zoli
 */
public class ModelBeanRegister {
    
    public static List<ModelBean> getModelBeans() {
        return BeanRegister.getBeans(ModelBean.class);
    }
    
}