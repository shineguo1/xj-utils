package org.xj.commons.toolkit;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/4/23 11:21
 */
public class Maps {


    public static <K,V> Map<K,V> asMap(Object[][] entries){
        return Stream.of(entries)
            .collect(Collectors.toMap(entry -> (K) entry[0], entry -> (V) entry[1]));
    }

    public static void main(String[] args) {
        Map<String, String> map1 = asMap(new String[][]{{"k1", "v1"}, {"k2", "v2"}});
        Map<String, Integer> map2 = asMap(new Object[][]{{"k1", 1}, {"k2", 2}});
    }
}
