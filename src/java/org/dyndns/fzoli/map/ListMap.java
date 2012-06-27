package org.dyndns.fzoli.map;

import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author zoli
 */
public class ListMap<K, V> extends HashMap<K, List<V>> {

    public ListMap() {
        super();
    }

    public ListMap(ListMap<K, V> m) {
        super(m);
    }
    
    public ListMap(Map<K, V[]> m) {
        super(arrMapToLsMap(m));
    }
    
    public ListMap(int initialCapacity) {
        super(initialCapacity);
    }

    public ListMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    
    @Override
    public List<V> get(Object key) {
        return getList((K) key);
    }
    
    public V getFirst(K key) {
        List<V> l = get(key);
        return (!l.isEmpty()) ? l.get(0) : null;
    }
    
    public void setFirst(K key, V value) {
        List<V> l = get(key);
        if (l.isEmpty()) l.add(value);
        else l.set(0, value);
    }
    
    @Override
    public List<V> put(K key, List<V> value) {
        List<V> l = get(key);
        List<V> old = new ArrayList<V>(l);
        l.addAll(value);
        return old;
    }
    
    public List<V> put(K key, V[] value) {
        return put(key, Arrays.asList(value));
    }
    
    private List<V> getList(K key) {
        List<V> l = super.get(key);
        if (l == null) {
            l = new ArrayList<V>();
            super.put(key, l);
        }
        return l;
    }
    
    private static <K, V> Map<K, List<V>> arrMapToLsMap(Map<K, V[]> arrMap) {
        Map<K, List<V>> lsMap = new HashMap<K, List<V>>();
        if (arrMap != null) {
            Iterator<Entry<K, V[]>> it = arrMap.entrySet().iterator();
            while (it.hasNext()) {
                Entry<K, V[]> e = it.next();
                lsMap.put(e.getKey(), Arrays.asList(e.getValue()));
            }
        }
        return lsMap;
    }
    
}