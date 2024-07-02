package org.xj.commons.toolkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xj
 * @date 2022/5/12 18:29
 */
public class CollectionUtils {


    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    /**
     * 校验集合是否为空
     *
     * @param coll 入参
     * @return boolean
     */
    public static boolean isEmpty(Collection<?> coll) {
        return (coll == null || coll.isEmpty());
    }

    /**
     * 校验集合是否不为空
     *
     * @param coll 入参
     * @return boolean
     */
    public static boolean isNotEmpty(Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * 判断Map是否为空
     *
     * @param map 入参
     * @return boolean
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return (map == null || map.isEmpty());
    }

    /**
     * 判断Map是否不为空
     *
     * @param map 入参
     * @return boolean
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    /**
     * 判断集合中所有内容是否全非null
     *
     * @param c   集合
     * @param <T> 集合泛型
     * @return 集合内容全部非null
     */
    public static <T> boolean containNull(Collection<? extends T> c) {
        if (c == null) {
            return true;
        }
        return c.stream().anyMatch(Objects::isNull);
    }

    /**
     * 用来过渡下Jdk1.8下ConcurrentHashMap的性能bug
     * https://bugs.openjdk.java.net/browse/JDK-8161372
     *
     * @param concurrentHashMap ConcurrentHashMap 没限制类型了，非ConcurrentHashMap就别调用这方法了
     * @param key               key
     * @param mappingFunction   function
     * @param <K>               k
     * @param <V>               v
     * @return V
     * @since 3.4.0
     */
    public static <K, V> V computeIfAbsent(Map<K, V> concurrentHashMap, K key, Function<? super K, ? extends V> mappingFunction) {
        V v = concurrentHashMap.get(key);
        if (v != null) {
            return v;
        }
        return concurrentHashMap.computeIfAbsent(key, mappingFunction);
    }

    // 提供处理Map多key取值工具方法

    /**
     * 批量取出Map中的值
     *
     * @param map  map
     * @param keys 键的集合
     * @param <K>  key的泛型
     * @param <V>  value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(Map<K, V> map, Iterable<K> keys) {
        List<V> result = new ArrayList<>();
        if (map != null && !map.isEmpty() && keys != null) {
            keys.forEach(key -> Optional.ofNullable(map.get(key)).ifPresent(result::add));
        }
        return result;
    }

    /**
     * 批量取出Map中的值
     *
     * @param map        map
     * @param keys       键的集合
     * @param comparator 排序器
     * @param <K>        key的泛型
     * @param <V>        value的泛型
     * @return value的泛型的集合
     */
    public static <K, V> List<V> getCollection(Map<K, V> map, Iterable<K> keys, Comparator<V> comparator) {
        Objects.requireNonNull(comparator);
        List<V> result = getCollection(map, keys);
        Collections.sort(result, comparator);
        return result;
    }


    /**
     * 安全地把集合c加进集合collection
     *
     * @param collection 主集合
     * @param c          增加的集合
     * @param <T>        集合泛型
     */
    public static <Collector extends Collection<T>, T> Collector addAll(Collector collection, Collection<? extends T> c) {
        if (Objects.nonNull(collection) && isNotEmpty(c)) {
            collection.addAll(c);
        }
        return collection;
    }

    @SuppressWarnings("unchecked")
    public static <T, V extends T> Collection<T> addAll(Collection<T> collection, V[] elements) {
        if (Objects.nonNull(collection) && Objects.nonNull(elements)) {
            Collections.addAll(collection, elements);
        }
        return collection;
    }

    @SafeVarargs
    public static <Collector extends Collection<T>, T> Collector merge(Collector ret, Collection<? extends T>... collections) {
        if (collections == null) {
            return ret;
        }
        for (Collection<? extends T> collection : collections) {
            addAll(ret, collection);
        }
        return ret;
    }

    public static <T> Stream<T> mergeToStream(Collection<? extends T>... collections) {
        Stream<T> ret = Stream.empty();
        if (collections == null) {
            return ret;
        }
        for (Collection<? extends T> collection : collections) {
            ret = Stream.concat(ret, toStream(collection));
        }
        return ret;
    }

    /**
     * 根据指定字段对列表进行分组
     */
    public static <T, K, U> Map<K, U> toMap(Collection<T> list,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper,
                                            BinaryOperator<U> mergeFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return MyCollections.newHashMap();
        }
        return list.stream().filter(Objects::nonNull).collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }

    /**
     * 根据指定字段对列表进行分组
     */
    public static <T, K, U> Map<K, U> toMap(Stream<T> stream,
                                            Function<? super T, ? extends K> keyMapper,
                                            Function<? super T, ? extends U> valueMapper,
                                            BinaryOperator<U> mergeFunction) {
        if (Objects.isNull(stream)) {
            return MyCollections.newHashMap();
        }
        return stream.filter(Objects::nonNull).collect(Collectors.toMap(keyMapper, valueMapper, mergeFunction));
    }

    public static <E> Stream<E> toStream(Collection<E> collection) {
        return collection == null ? Stream.empty() : collection.stream();
    }

    /**
     * 根据指定字段对列表进行分组
     */
    public static <T, R> Map<R, List<T>> group(Collection<T> list, Function<T, R> function) {
        if (CollectionUtils.isEmpty(list)) {
            return MyCollections.newHashMap();
        }
        return list.stream().filter(Objects::nonNull).collect(Collectors.groupingBy(function));
    }

    /**
     * 根据指定字段对列表进行分组
     */
    public static <T, K, V> Map<K, List<V>> group(Collection<T> list, Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (CollectionUtils.isEmpty(list)) {
            return MyCollections.newHashMap();
        }
        return list.stream()
                .filter(o -> Objects.nonNull(o) && Objects.nonNull(keyFunction.apply(o)))
                .collect(Collectors.groupingBy(keyFunction, Collectors.mapping(valueFunction, Collectors.toList())));
    }

    public static <T, K, V> Map<K, List<V>> group(Stream<T> stream, Function<T, K> keyFunction, Function<T, V> valueFunction) {
        if (Objects.isNull(stream)) {
            return MyCollections.newHashMap();
        }
        return stream
                .filter(o -> Objects.nonNull(o) && Objects.nonNull(keyFunction.apply(o)))
                .collect(Collectors.groupingBy(keyFunction, Collectors.mapping(valueFunction, Collectors.toList())));
    }

    /**
     * 说明:
     * 将collection集合拆分,每size个元素拆分成一个新的子集合.
     * 拆分过程有序. 按下标划分, [0, size)为第一个子集合, [size, 2*size)为第二个子集合,依次类推.
     * <p>
     * 举例:
     * 输入 collection = [0,1,2,3,4,5,6,7,8,9,10], size = 3
     * 输出 [[0, 1, 2], [3, 4, 5], [6, 7, 8], [9, 10]]
     *
     * @param collection 集合
     * @param size       子集合size
     * @param <V>        集合泛型
     * @return 拆分后子集合数组
     */
    public static <V> List<List<V>> split(Collection<V> collection, int size) {
        if (collection == null || collection.size() == 0) {
            return MyCollections.newArrayList();
        }
        List<List<V>> retList = MyCollections.newArrayList();
        int index = 0;
        for (V v : collection) {
            List<V> innerList;
            if (index++ % size == 0) {
                innerList = MyCollections.newArrayList();
                retList.add(innerList);
            } else {
                innerList = retList.get(retList.size() - 1);
            }
            innerList.add(v);
        }
        return retList;
    }

    /**
     * @see #split(Collection, int)
     */
    public static <K, V> List<List<Map.Entry<K, V>>> split(Map<K, V> map, int size) {
        if (map == null || map.size() == 0) {
            return MyCollections.newArrayList();
        }
        Set<Map.Entry<K, V>> entries = map.entrySet();
        return split(entries, size);
    }

    /**
     * if 布尔表达式(b) 为真， 将元素element加入集合collection
     */
    public static <V> void addIf(Collection<V> collection, V element, boolean b) {
        if (b) {
            collection.add(element);
        }
    }

    /**
     * if 布尔表达式(b) 为真， 将键值对<k,v>加入map
     */
    public static <K, V> void putIf(Map<K, V> map, K k, V v, boolean b) {
        if (b) {
            map.put(k, v);
        }
    }


    /**
     * 从map中取数组。如果key不存在，则初始化空数组。
     */
    public static <K, V> List<V> mapGet(Map<K, List<V>> map, K key) {
        List<V> list = map.get(key);
        if (list == null) {
            list = MyCollections.newArrayList();
            map.put(key, list);
        }
        return list;
    }

    public static int sizeOf(Collection c) {
        return isEmpty(c) ? 0 : c.size();
    }

    public static int sizeOf(Map c) {
        return isEmpty(c) ? 0 : c.size();
    }

    public static <T> List<T> toListIfNotNull(T obj) {
        return obj == null ? null : Collections.singletonList(obj);
    }

    public static boolean notNullAndEmpty(Collection collection) {
        return collection != null && collection.size() == 0;
    }

    public static <I, T extends I> List<I> toSuper(List<T> list, Class<I> clazz) {
        return (List<I>) list;
    }

}
