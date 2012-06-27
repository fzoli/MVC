package org.dyndns.fzoli.mvc.test.server.model;

import org.dyndns.fzoli.mvc.common.request.map.RequestMap;
import org.dyndns.fzoli.mvc.server.model.JSONModel;
import org.dyndns.fzoli.mvc.test.common.key.TestModelKeys;
import org.dyndns.fzoli.mvc.test.common.pojo.TestData;
import org.dyndns.fzoli.mvc.test.common.pojo.TestEvent;

public class TestModel extends JSONModel<TestEvent, TestData> implements TestModelKeys {
    
    private String str = "Világ", str2 = "Picur";
    
    public String getStr() {
        return str;
    }

    public String getStr2() {
        return str2;
    }
    
    /**
     * Set the test string.
     * @param str
     * @return -1 if str is null
     * @return 0 if str is accepted
     * @return 1 if str is unaccepted
     */
    public int setStr(String str) {
        return setStr(VAL_STR, str);
    }
    
    public int setStr2(String str) {
        return setStr(VAL_STR_2, str);
    }
    
    private int setStr(String key, String s) {
        if (s == null) return -1;
        if (isDeny(s)) return 1;
        String old = key.equals(VAL_STR) ? str : str2;
        if (!s.equals(old)) {
            if (key.equals(VAL_STR)) str = s;
            else str2 = s;
            addEvent(new TestEvent(key, s));
        }
        return 0;
    }
    
    protected boolean isDeny(String s) {
        return s.equals("Éva") || s.equals("Ádám");
    }
    
    @Override
    protected TestData getProperties(RequestMap request) {
        return new TestData(getStr(), getStr2());
    }

    @Override
    protected int setProperty(RequestMap request) {
        String prop = request.getFirst(MSG_PROPERTY);
        String val = request.getFirst(MSG_VALUE);
        if (prop != null && val != null) {
            if(prop.equals(VAL_STR)) return setStr(val);
            if(prop.equals(VAL_STR_2)) return setStr2(val);
        }
        return -1;
    }
    
}