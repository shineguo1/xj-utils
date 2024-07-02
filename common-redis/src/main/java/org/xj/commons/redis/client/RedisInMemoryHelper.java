package org.xj.commons.redis.client;

import com.alibaba.fastjson2.TypeReference;
import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.toolkit.DateUtil;
import org.xj.commons.toolkit.LRUCache;
import org.xj.commons.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用途：Redis缓存大对象时(转成JSON字符串长度大于DATA_MAX_SIZE)，在内存中保存这个对象。查询key时，如果在redis中该key未过期，可以直接从内存中复用对象。意图是避免频繁创建和垃圾回收大对象。
 * <p/>
 * 将查询大对象(超大字符串)转化为查询大对象缓存版本(时间戳),降低数据传输和json转换的网络/cpu/内存开销
 *
 * @author xj
 * @version 1.0.0 createTime:  2023/5/17 9:28
 */
@Slf4j
public class RedisInMemoryHelper {

    private static int DATA_MAX_SIZE = 1 * 1024 * 1024;
    private final static RedisInMemoryHelper instance = new RedisInMemoryHelper();

    public static RedisInMemoryHelper get() {
        return instance;
    }

    public static void setDataMaxSize(int i) {
        DATA_MAX_SIZE = i;
    }

    public static int getDataMaxSize() {
        return DATA_MAX_SIZE;
    }

    @Getter
    private final LRUCache<String, Integer> cacheAnalysis = new LRUCache<>(255);

    @Getter
    private final LRUCache<String, MetaObject<?>> cache = new LRUCache<>(255);

    // Redis String数据结构
    //--------------------------------------------

    public <T> T stringGet(String key, TypeReference<T> typeReference) {
        try {
            Class<? super T> clazz = typeReference.getRawType();
            Object object = stringGet(key, clazz);
            return (T) object;
        } catch (Exception e) {
            log.error("cause:", e);
            return null;
        }
    }

    public <T> T stringGet(String key, Class<T> clazz) {
        try {
            MetaObject<?> metaObj = cache.get(key);
            //如果内存缓存类型匹配，并且该key在redis上未过期，则跳过big object转换，返回内存中的缓存结果
            if (metaObj != null && metaObj.value != null && clazz.isAssignableFrom(metaObj.value.getClass())) {
                if (RedisManager.get().exist(key) && validateVersion(key, metaObj)) {
                    return (T) metaObj.value;
                } else {
                    //redis中过期，同步删除内存中的缓存对象
                    cache.invalidate(key);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("cause:", e);
            return null;
        }
    }

    /**
     * 如果内存中对象为空，或者内存中的版本和redis中的缓存版本不一致，表示没有命中乐观锁（其他服务器更新了数据）。
     * 此时内存中的缓存已落后版本，不可用。返回false。
     * <p>
     * 异常情况的补偿：
     * 如果意外发现redis中没有储存乐观锁，临时把当前时间戳存入redis乐观锁。这样会使得所有应用实例重新从redis中同步数据至内存。
     *
     * @param key        查询对象的redisKey
     * @param metaObject 内存中的缓存元对象
     * @return
     */
    private boolean validateVersion(String key, MetaObject<?> metaObject) {
        try {
            if (metaObject == null) {
                return false;
            }
            String versionKey = versionKey(key);
            Long versionInRedis = RedisManager.get().queryObjectByKey(versionKey, Long.class);
            //存在数据但缺少乐观锁，临时添加一个当前时间乐观锁
            if (versionInRedis == null) {
                RedisManager.get().insertObject(versionKey, DateUtil.getEpochByNow(), TimeUnit.DAYS.toSeconds(1));
            }
            //不使用Objects.equals判断，因为假如内存中和redis中的版本都是null，工具方法会返回true。但其实这是异常情况，还是返回false稳妥。
            return versionInRedis != null && metaObject.version >= versionInRedis;
        } catch (Exception e) {
            log.error("cause:", e);
            return false;
        }
    }

    /**
     * 使用版本号作为乐观锁，保证分布式数据一致性
     */
    private String versionKey(String key) {
        return key + ":VERSION";
    }

    public <T> List<T> stringGetAsList(String key, Class<T> clazz) {
        try {
            MetaObject<?> metaObj = cache.get(key);
            // 期望拿到泛型为clazz的List返回结果
            // 如果内存缓存类型匹配，并且该key在redis上未过期，则跳过big object转换，返回内存中的缓存结果
            // instanceof可以判null
            if (metaObj != null && metaObj.value instanceof Collection) {
                if (RedisManager.get().exist(key) && validateVersion(key, metaObj)) {
                    Object obj = metaObj.value;
                    List list = obj instanceof List ? (List) obj : new ArrayList((Collection) obj);
                    //大对象不可能是长度为0的数组，所以一定可以取第一个元素判断泛型类型。
                    if (CollectionUtils.isNotEmpty(list) && clazz.isAssignableFrom(list.get(0).getClass())) {
                        return list;
                    }
                } else {
                    //redis中过期，同步删除内存中的缓存对象
                    cache.invalidate(key);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("cause:", e);
            return null;
        }
    }

    /**
     * redis缓存失效，从数据源重新查得数据时，需要updateVersion。
     * 内存中乐观锁版本落后，从redis中同步数据到内存，不能updateVersion，否则会导致其他应用实例乐观锁版本落后，进而继续更新版本号，死循环。
     *
     * @param expireTime    单位：毫秒
     * @param updateVersion 是否需要更新reids中的乐观锁
     */
    public void stringPut(String key, Object object, String jsonString, long expireTime, boolean updateVersion) {
        try {
            int length = StringUtils.length(jsonString);
            if (length > DATA_MAX_SIZE) {
                Long version = DateUtil.getEpochByNow();
                if (updateVersion) {
                    String versionKey = versionKey(key);
                    RedisManager.get().insertObject(versionKey, version, (expireTime - version) / 1000 + 1);
                }
                cache.put(key, new MetaObject<>(version, expireTime, object));
                cacheAnalysis.put(key, length);
            }
        } catch (Exception e) {
            log.error("cause:", e);
        }
    }

    // Redis Hash数据结构
    //--------------------------------------------

    public <T> T hashGet(String key, String field, TypeReference<T> typeReference) {
        try {
            Class<? super T> clazz = typeReference.getRawType();
            String inMemoryKey = hashKey(key, field);
            MetaObject metaObject = cache.get(inMemoryKey);
            //如果内存缓存类型匹配，并且在内存中未逻辑过期,并且该key在redis上未过期，则避免json字符串转换大对象，直接返回内存中的缓存结果
            if (metaObject != null && clazz.isInstance(metaObject.value)) {
                //逻辑过期时间
                long expiredTimestamp = metaObject.expireTimestamp;
                if (/*校验逻辑过期时间*/expiredTimestamp > DateUtil.getEpochByNow() &&
                        /*校验redis是否过期*/  RedisManager.get().mapExist(key, field) &&
                        /*校验乐观锁版本*/ validateVersion(inMemoryKey, metaObject)) {
                    return (T) metaObject.value;
                } else {
                    //内存中逻辑过期, 或redis中过期，同步删除内存中的缓存对象
                    cache.invalidate(key);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("cause:", e);
            return null;
        }
    }

    /**
     * @param expireTimestamp 单位：毫秒
     */
    public void hashPut(String key, String field, Object object, String jsonString, Long expireTimestamp, boolean updateVersion) {
        String inMemoryKey = hashKey(key, field);
        //将key和field组合成新key，其他与string结构一样
        stringPut(inMemoryKey, object, jsonString, expireTimestamp, updateVersion);
    }

    private String hashKey(String key, String field) {
        return "HASH_" + key + "_" + field;
    }

    // Redis ZSet数据结构
    //--------------------------------------------

    public Object zsetGet(String key, Object object) {
        throw new UnsupportedOperationException("zsetGet 方法未实现");
    }

    public void zsetPut(String key, Object object, String jsonString) {
        throw new UnsupportedOperationException("zsetPut 方法未实现");
    }

    @Data
    @AllArgsConstructor
    static class MetaObject<T> {
        /**
         * 乐观锁（版本号），保证分布式数据一致性。
         * <p>
         * 使用记入缓存的时间戳作为乐观锁。当内存版本号大于等于redis版本号，认为内存中的缓存有效
         * （因为在分布式环境，可能由别的应用实例先存入redis，一段时间后同步到本应用实例，所以本应用实例的版本号(时间戳)会大于redis内的版本号，此时也有效）
         */
        private long version;
        /**
         * 逻辑过期时间（毫秒）
         */
        private long expireTimestamp;
        /**
         * 缓存值
         */
        private T value;
    }

}
