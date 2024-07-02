package org.xj.commons.toolkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/14 18:13
 */
public class MyCollections {
    public static <K, V> HashMap<K, V> newHashMap() {
        return new HashMap<>();
    }

    public static <V> ArrayList<V> newArrayList() {
        return new ArrayList<>();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet<>();
    }
}
