package org.dyndns.fzoli.mvc.common.map;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author zoli
 */
public class CommonMap<K, V> extends HashMap<K, V> {

    public CommonMap() {
        super();
    }

    public CommonMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    public CommonMap(int initialCapacity) {
        super(initialCapacity);
    }

    public CommonMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        Iterator<? extends K> i = m.keySet().iterator();
        while (i.hasNext()) {
            K key = i.next();
            V value = m.get(key);
            put(key, value);
        }
    }
    
    @Override
    public void clear() {
        Iterator<K> i = keySet().iterator();
        while (i.hasNext()) {
            remove(i.next());
            clear();
            break;
        }
    }
    
}