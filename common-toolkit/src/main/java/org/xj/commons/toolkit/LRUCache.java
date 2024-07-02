package org.xj.commons.toolkit;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/8/9 14:03
 */
public class LRUCache<K,V> extends LinkedHashMap<K,V>{

    private int cap;
    private static final long serialVersionUID = 1L;

    public LRUCache(int cap) {
        super(16, 0.75f, true);
        this.cap = cap;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > cap;
    }

    public void invalidate(K key) {
        this.remove(key);
    }
}
