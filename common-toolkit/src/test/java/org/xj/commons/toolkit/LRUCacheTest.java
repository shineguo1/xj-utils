package org.xj.commons.toolkit;

class LRUCacheTest {

    public static void main(String[] args) {
        LRUCache<Integer, String> cache = new LRUCache<>(10);
        cache.put(1,"1");
        cache.put(2,"1");
        cache.put(3,"1");
        System.out.println(cache);
        cache.get(1);
        cache.put(4,"1");
        System.out.println(cache);
        cache.put(5,"1");
        cache.put(6,"1");
        cache.put(7,"1");
        cache.put(8,"1");
        cache.put(9,"1");
        cache.get(2);
        cache.put(10,"1");
        System.out.println(cache);
        cache.put(11,"1");
        System.out.println(cache);
        cache.put(12,"1");
        System.out.println(cache);
    }



}