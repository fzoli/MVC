package org.dyndns.fzoli.mvc.test.server.model;

import org.dyndns.fzoli.mvc.test.common.pojo.TestEvent;

/**
 *
 * @author zoli
 */
public class MyTestModel extends TestModel {

    private static String str2 = "Mindenki";
    
    public MyTestModel() {
        setStr("Én", false);
    }

    @Override
    public String getStr2() {
        return str2;
    }

    @Override
    public int setStr2(String s) {
        if (s == null) return -1;
        if (isDeny(s)) return 1;
        if (!s.equals(getStr2())) {
            str2 = s;
            addStaticEvent(new TestEvent(TestEvent.VAL_STR_2, s));
        }
        return 0;
    }
    
    private int setStr(String str, boolean sleep) {
        if (sleep) try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {}
        return super.setStr(str);
    }
    
    @Override
    public int setStr(String str) {
        return setStr(str, true);
    }
    
}