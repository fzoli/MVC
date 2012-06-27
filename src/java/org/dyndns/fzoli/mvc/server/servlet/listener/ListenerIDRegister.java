package org.dyndns.fzoli.mvc.server.servlet.listener;

import java.util.Map.Entry;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBean;
import org.dyndns.fzoli.mvc.server.model.bean.ModelBeanRegister;

class ListenerIDListGC extends TimerTask {

    private final ListenerIDRegister LOCK;
    private final List<String> LISTENER_IDS;
    private final Map<HttpServletRequest, String> USED_LISTENER_IDS;
    private final Map<String, Integer> REQ_COUNTER;
    private final Map<String, String> SESS_IDS;
    private final Map<String, Date> LAST_REG_DATES;
    private final int RECONN_WAIT;
    private final Log LOG = LogFactory.getLog(ListenerIDRegister.class);
    
    public ListenerIDListGC(ListenerIDRegister lock, List<String> listenerIds, Map<HttpServletRequest, String> usedListenerIds, Map<String, Date> lastRegDates, Map<String, String> sessIds, Map<String, Integer> reqCounter, int reconnectWait) {
        LOCK = lock;
        SESS_IDS = sessIds;
        LISTENER_IDS = listenerIds;
        LAST_REG_DATES = lastRegDates;
        USED_LISTENER_IDS = usedListenerIds;
        REQ_COUNTER = reqCounter;
        RECONN_WAIT = reconnectWait;
    }
    
    private void removeId(String id) {
        SESS_IDS.remove(id);
        REQ_COUNTER.remove(id);
        LISTENER_IDS.remove(id);
        LAST_REG_DATES.remove(id);
        List<ModelBean> mbs = ModelBeanRegister.getModelBeans();
        for (ModelBean mb : mbs) {
            mb.removeListeners(id);
        }
    }
    
    @Override
    public void run() {
            synchronized(LOCK) {
                Date lockDate = new Date();
                boolean finished = false;
                while(!finished) {
                    if (LISTENER_IDS.isEmpty()) finished = true;
                    for (String id : LISTENER_IDS) {
                        if (!USED_LISTENER_IDS.containsValue(id)) {
                            if (lockDate.getTime() - LAST_REG_DATES.get(id).getTime() >= RECONN_WAIT) {
                                removeId(id);
                                finished = false;
                                if (LOG.isInfoEnabled()) LOG.info("LISTENER ID REMOVED: " + id);
                                break;
                            }
                        }
                        finished = true;
                    }
                }
            }
    }
    
}

public class ListenerIDRegister {
    
    private static final List<String> LISTENER_IDS = new ArrayList<String>();
    
    private final Map<String, Integer> REQ_COUNTER = new HashMap<String, Integer>();
    private final Map<HttpServletRequest, String> USED_LISTENER_IDS = new HashMap<HttpServletRequest, String>();
    private final Map<String, Date> LAST_REG_DATES = new HashMap<String, Date>();
    private final Map<String, String> SESS_IDS = new HashMap<String, String>();
    private final ListenerIDListGC GC_TASK;
    private final Timer GC_TIMER = new Timer();

    public ListenerIDRegister(int reconnectWait, int gcDelay) {
        GC_TASK = new ListenerIDListGC(this, LISTENER_IDS, USED_LISTENER_IDS, LAST_REG_DATES, SESS_IDS, REQ_COUNTER, reconnectWait);
        GC_TIMER.schedule(GC_TASK, 0, gcDelay);
    }
    
    @Override
    protected void finalize() throws Throwable {
        GC_TIMER.cancel();
        super.finalize();
    }
    
    public void increaseRequestCounter(String id) {
        int count = getRequestCounter(id);
        count++;
        synchronized(REQ_COUNTER) {
            REQ_COUNTER.put(id, count);
        }
    }
    
    public int getRequestCounter(String id) {
        synchronized(REQ_COUNTER) {
            Integer integer = REQ_COUNTER.get(id);
            return integer == null ? 0 : integer;
        }
    }
    
    public List<HttpServletRequest> getRequestsWithListenerId(String id) {
        synchronized(USED_LISTENER_IDS) {
            List<HttpServletRequest> l = new ArrayList<HttpServletRequest>();
            Iterator<Entry<HttpServletRequest, String>> it = USED_LISTENER_IDS.entrySet().iterator();
            while (it.hasNext()) {
                Entry<HttpServletRequest, String> e = it.next();
                if (e.getValue().equals(id)) l.add(e.getKey());
            }
            return l;
        }
    }
    
    public static boolean isListenerIdExists(String id) {
        if (id == null) return false;
        synchronized(LISTENER_IDS) {
            return LISTENER_IDS.contains(id);
        }
    }
    
    public static void addListenerId(String id) {
        synchronized(LISTENER_IDS) {
            LISTENER_IDS.add(id);
        }
    }
    
    public void registerListenerId(HttpServletRequest request, String id) {
        synchronized(USED_LISTENER_IDS) {
            USED_LISTENER_IDS.put(request, id);
            updateRegTime(id);
        }
    }
    
    public void updateRegTime(String id) {
        synchronized(LAST_REG_DATES) {
            LAST_REG_DATES.put(id, new Date());
        }
    }
    
    public boolean isListenerServletRegistrated(HttpServletRequest request) {
        synchronized(USED_LISTENER_IDS) {
            return USED_LISTENER_IDS.containsKey(request);
        }
    }
    
    public boolean isSessionIdEquals(String listenerId, String sessionId) {
        if (listenerId == null) return false;
        synchronized(SESS_IDS) {
            String sessid = SESS_IDS.get(listenerId);
            if (sessid == null) return false;
            return sessid.equals(sessionId);
        }
    }
    
    public void setSessionId(String listenerId, String sessionId) {
        synchronized(SESS_IDS) {
            SESS_IDS.put(listenerId, sessionId);
        }
    }
    
    public void unregisterListenerId(HttpServletRequest request) {
        synchronized(USED_LISTENER_IDS) {
            USED_LISTENER_IDS.remove(request);
        }
    }
    
}