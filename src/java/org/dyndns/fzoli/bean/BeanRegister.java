package org.dyndns.fzoli.bean;

import java.util.*;

/**
 * AbstractBean konténer.
 * @author Farkas Zoltán
 */
public class BeanRegister {
    
    private static final List<AbstractBean> BEANS = new ArrayList<AbstractBean>();

    private static Timer gcTimer;
    
    private static final TimerTask GC = new TimerTask() {

        @Override
        public void run() {
            synchronized (BEANS) {
                Date now = new Date();
                List<AbstractBean> removeables = new ArrayList<AbstractBean>();
                for (AbstractBean b : BEANS) {
                    if (b.getExpireDate() != null && b.getExpireDate().before(now)) {
                        removeables.add(b);
                        b.onExpire();
                    }
                }
                BEANS.removeAll(removeables);
            }
        }

    };
    
    static {
        startGC();
    }
    
    /**
     * Nem példányosítható az osztály.
     */
    private BeanRegister() {
    }
    
    public static void startGC() {
        stopGC();
        gcTimer = new Timer();
        gcTimer.schedule(GC, 0, 600000);
    }
    
    public static void stopGC() {
        if (gcTimer != null) gcTimer.cancel();
    }
    
    /**
     * Az összes regisztrált bean objektumot adja vissza.
     * @return AbstractBean lista
     */
    public static List<AbstractBean> getBeans() {
        return getBeans(AbstractBean.class);
    }
    
    /**
     * Az összes regisztrált, T típusú bean objektumot adja vissza.
     * @param clazz szűrő feltétel
     * @return AbstractBean szűrt lista
     */
    public static <T extends AbstractBean> List<T> getBeans(Class<T> clazz) {
        List<T> l = new ArrayList<T>();
        if (clazz == null) return l;
        synchronized(BEANS) {
            for (AbstractBean b : BEANS) {
                if (clazz.isInstance(b)) l.add((T)b);
            }
        }
        return l;
    }
    
    /**
     * Bean hozzáadása.
     * @param bean a regisztrálandó bean objektum
     */
    public static void addBean(AbstractBean bean) {
        synchronized(BEANS) {
            BEANS.add(bean);
        }
    }
    
    /**
     * Bean törlése.
     * @param bean a törlendő bean objektum
     */
    public static void removeBean(AbstractBean bean) {
        synchronized(BEANS) {
            BEANS.remove(bean);
        }
    }
    
}