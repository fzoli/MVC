package org.dyndns.fzoli.bean;

import java.io.Serializable;
import java.util.Date;

/**
 * Ennek az osztálynak a példányai létrejöttükkor regisztrálják magukat a
 * BeanRegister konténerbe, megszűnésekor törlik magukat belőle.
 * @author Farkas Zoltán
 */
public abstract class AbstractBean implements Serializable {

    public AbstractBean() {
        BeanRegister.addBean(this);
    }

    public abstract Date getExpireDate();
    
    public void onExpire() {
        ;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            BeanRegister.removeBean(this);
        }
        catch (Exception ex) {
            ;
        }
        super.finalize();
    }
    
}