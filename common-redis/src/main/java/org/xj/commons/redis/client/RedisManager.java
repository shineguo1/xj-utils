package org.xj.commons.redis.client;

import com.alibaba.fastjson2.*;
import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.toolkit.DateUtil;
import org.xj.commons.toolkit.MyCollections;
import org.xj.commons.toolkit.StringUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.config.Config;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * redis服务
 *
 * @author xj
 * @version 1.0.0 createTime: 2016/4/15
 */
@Slf4j
@Service
public class RedisManager implements IRedisManager {

    public static final Duration MAP_EXPIRE_DURATION = Duration.ofDays(30);

    @Getter
    private RedissonClient redissonClient;

    private static final RedisManager SINGLETON = new RedisManager();

    public static RedisManager get() {
        return SINGLETON;
    }

    public void setClient(Config config) {
        redissonClient = Redisson.create(config);
    }

    public void setClient(RedissonClient redisson) {
        redissonClient = redisson;
    }

    /**
     * 1、查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @param clazz   指定返回List内存放的对象类型
     * @param <T>     返回对象类型,集合泛型
     * @return List<T>返回对象集合
     */
    public <T> List<T> queryListByKey(final String keyEnum, final Class<T> clazz, JSONReader.Feature... features) {
        log.debug("queryListByKey request:{}", keyEnum);
        try {
            String key = key(keyEnum);
            //1. 是否命中内存大对象缓存
            List<T> ts = RedisInMemoryHelper.get().stringGetAsList(key, clazz);
            if (ts != null) {
                return ts;
            }
            //2. 查redis
            String resultStr = queryObjectByKey(key);
            if (StringUtils.isBlank(resultStr)) {
                return null;
            }
            List<T> value = JSONArray.parseArray(resultStr, features).toJavaList(clazz, features);
            log.debug("queryListByKey response:{}", value.toString());
            //3. 如果是大对象，补偿进内存缓存
            RedisInMemoryHelper.get().stringPut(key, value, resultStr, Long.MAX_VALUE, false);
            return value;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * 查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @return <T> T
     */
    @SuppressWarnings("DuplicatedCode")
    public <T> T queryObjectByKey(String keyEnum, Class<T> clazz, JSONReader.Feature... features) {
        log.debug("queryObjectByKey request:{}", keyEnum);
        try {
            //1. 是否命中内存大对象缓存
            String key = key(keyEnum);
            T ts = RedisInMemoryHelper.get().stringGet(key, clazz);
            if (ts != null) {
                return ts;
            }
            //2. 查redis
            String resultStr = queryObjectByKey(key);
            if (StringUtils.isBlank(resultStr)) {
                return null;
            }
            T value = clazz == String.class ? (T) resultStr : JSONObject.parseObject(resultStr, clazz, features);
            log.debug("queryObjectByKey response:{}", value.toString());
            //3. 如果是大对象，补偿进内存缓存
            RedisInMemoryHelper.get().stringPut(key, value, resultStr, Long.MAX_VALUE, false);
            return value;
        } catch (Exception e) {
            log.error("helper error:", e);
        }
        return null;
    }

    /**
     * 查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @return <T> T
     */
    @SuppressWarnings("DuplicatedCode")
    public <T> T queryObjectByKey(String keyEnum, TypeReference<T> typeReference, JSONReader.Feature... features) {
        log.debug("queryObjectByKey request:{}", keyEnum);
        try {
            //1. 是否命中内存大对象缓存
            String key = key(keyEnum);
            T ts = RedisInMemoryHelper.get().stringGet(key, typeReference);
            if (ts != null) {
                return ts;
            }
            //2. 查redis
            String resultStr = queryObjectByKey(key);
            if (StringUtils.isBlank(resultStr)) {
                return null;
            }
            T value = JSONObject.parseObject(resultStr, typeReference, features);
            log.debug("queryObjectByKey response:{}", value.toString());
            return value;
        } catch (Exception e) {
            log.error("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：String， 命令：DEL key
     * 删除
     *
     * @param keyEnum
     * @return
     */
    public Boolean deleteObjectByKey(String keyEnum) {
        log.debug("deleteObjectByKey request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            RBucket<Object> bucket = redissonClient.getBucket(k);
            if (Objects.nonNull(bucket)) {
                Object andDelete = bucket.getAndDelete();
                log.debug("deleteObjectByKey response:{}", andDelete);
                return true;
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return false;
    }

    /**
     * 2、插入redis 数据库
     *
     * @param keyEnum 关键字
     * @param obj     保存对象
     * @return 对象类型, 泛型
     */
    public boolean insertObject(final String keyEnum, final Object obj) {
        return insertObject(keyEnum, obj, 30L);
    }

    /**
     * -- 元操作：数据结构：String， 命令：SET KEY VALUE
     * <p>
     * 3、插入redis 数据库,设置有效期
     *
     * @param keyEnum  关键字
     * @param obj      保存对象
     * @param timeout  有效期（秒）
     * @param timeout  有效期（秒）
     * @param features 序列化特性
     * @return 对象类型, 泛型
     */
    public boolean insertObject(final String keyEnum, final Object obj, final long timeout, JSONWriter.Feature... features) {
        log.debug("insertObject request:key={},obj={}", keyEnum, obj.toString());
        try {
            long timeoutRandom = getRandomDelay(timeout);
            final String k = key(keyEnum);
            final String value;
            value = toJSONString(obj, features);

            //1. 如果是大对象，插入内存缓存（先插入内存缓存，更新乐观锁版本号）
            RedisInMemoryHelper.get().stringPut(k, obj, value, timeoutRandom * 1000, true);

            //2. 插入redis
            RBucket bucket = redissonClient.getBucket(k);
            bucket.set(value, timeoutRandom, TimeUnit.SECONDS);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 随机延长0-20%的timeout
     */
    private long getRandomDelay(long timeout) {
        return timeout + Double.valueOf(Math.random() * timeout * 20 / 100).longValue();
    }

    private String toJSONString(Object obj, JSONWriter.Feature... features) {
        final String value;
        if (obj instanceof String) {
            value = obj.toString();
        } else {
            value = JSON.toJSONString(obj, features);
        }
        return value;
    }

    /**
     * -- 元操作：数据结构：String， 命令：SET KEY VALUE
     * <p>
     * 3、插入redis 数据库,设置有效期
     *
     * @param keyEnum 关键字
     * @param obj     保存对象
     * @return 对象类型, 泛型
     */
    public boolean insertObjectNotExpire(final String keyEnum, final Object obj) {
        log.debug("insertObject request:key={},obj={}", keyEnum, obj.toString());
        try {
            final String k = key(keyEnum);
            final String value = toJSONString(obj);
            //1. 如果是大对象，插入内存缓存（先插入内存缓存，更新乐观锁版本号）
            RedisInMemoryHelper.get().stringPut(k, obj, value, Long.MAX_VALUE, true);
            //2. 插入redis
            RBucket bucket = redissonClient.getBucket(k);
            bucket.set(value);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;

    }

    /**
     * -- 元操作：数据结构：String， 命令：EXISTS KEY
     * <p>
     * 3、插入redis 数据库,设置有效期
     *
     * @param keyEnum 关键字
     * @return 对象类型, 泛型
     */
    public boolean exist(final String keyEnum) {
        log.debug("exist request:key={},obj={}", keyEnum);
        try {
            final String k = key(keyEnum);
            RBucket bucket = redissonClient.getBucket(k);
            return bucket.isExists();
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;

    }

    /**
     * -- 元操作：数据结构：String， 命令：GET KEY  返回：JsonString
     * <p>
     * 6、查询redis 数据库
     *
     * @param keyEnum 查询关键字
     * @return String
     */
    public String queryObjectByKey(String keyEnum) {
        log.debug("queryObjectByKey request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            RBucket<String> bucket = redissonClient.getBucket(k);
            if (Objects.nonNull(bucket)) {
                String resultStr = bucket.get();
                log.debug("queryObjectByKey response:{}", resultStr);
                return resultStr;
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：HASH， 命令：HSET KEY FIELD VALUE
     * <p>
     * 往redis hash设置<field, value>键对
     * <pre class="code">
     * local v = redis.call('hget', KEYS[1], ARGV[1]);
     * redis.call('hset', KEYS[1], ARGV[1], ARGV[2]);
     * return v
     * </pre>
     *
     * @param keyEnum redis key
     * @param field   map field
     * @param value   map value
     * @param timeout filed 过期时间（秒）。 注意，如果map没有put操作，map有可能在{@link #MAP_EXPIRE_DURATION}后过期。
     * @return
     */
    public Object insertMapByField(String keyEnum, String field, Object value, Long timeout, JSONWriter.Feature... features) {
        log.debug("insertMapByField request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            RMap<Object, Object> map = redissonClient.getMap(k);
            //每次插入数据，刷新map30天缓存时间
            map.expire(MAP_EXPIRE_DURATION);
            long timeoutRandom = getRandomDelay(timeout);
            long expireTimestamp = DateUtil.getEpochByNow() + timeoutRandom * 1000;
            //构造结构体，存过期时间戳
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", value);
            jsonObject.put("expireTimestamp", expireTimestamp);
            String jsonString = JSON.toJSONString(jsonObject, features);
            //1. 先插入内存缓存，更新乐观锁版本号
            RedisInMemoryHelper.get().hashPut(k, field, value, jsonString, expireTimestamp, true);
            //2. 插入redis
            Object o = map.put(field, jsonString);
            if (Objects.nonNull(o)) {
                log.debug("insertMapByField response:{}", o);
                return o;
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：HASH， 命令：DEL KEY
     */
    public boolean deleteMap(String keyEnum) {
        try {
            final String k = key(keyEnum);
            RMap<Object, Object> map = redissonClient.getMap(k);
            return map.delete();
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return false;
    }

    /**
     * -- 元操作：数据结构：HASH， 命令：HGET KEY FIELD
     */
    public String queryMapByFiled(String keyEnum, String field) {
        log.debug("queryMapByFiled request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            RMap<Object, Object> map = redissonClient.getMap(k);
            Object o = map.get(field);
            if (Objects.nonNull(o)) {
                log.debug("queryMapByFiled response:{}", o);
                JSONObject jsonObj = JSONObject.parseObject((String) o);
                Long expireTimestamp = jsonObj.getLong("expireTimestamp");
                if (DateUtil.getEpochByNow() > expireTimestamp) {
                    //过期
                    return null;
                } else {
                    return jsonObj.getString("data");
                }
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：HASH， 命令：HGET KEY FIELD
     */
    public <T> T queryMapByFiled(String keyEnum, String field, TypeReference<T> typeReference) {
        log.debug("queryMapByFiled request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            //1. 是否命中内存大对象缓存
            T ts = RedisInMemoryHelper.get().hashGet(k, field, typeReference);
            if (ts != null) {
                return ts;
            }

            //2. 查redis
            RMap<Object, Object> map = redissonClient.getMap(k);
            Object o = map.get(field);
            if (Objects.nonNull(o)) {
                log.debug("queryMapByFiled response:{}", o);
                JSONObject jsonObj = JSONObject.parseObject((String) o);
                Long expireTimestamp = jsonObj.getLong("expireTimestamp");
                if (DateUtil.getEpochByNow() > expireTimestamp) {
                    //过期
                    return null;
                } else {
                    String jsonString = jsonObj.getString("data");
                    T ret = JSONObject.parseObject(jsonString, typeReference);
                    //倘若内存缓存失效,redis有缓存
                    //重新补充内存缓存
                    RedisInMemoryHelper.get().hashPut(k, field, ret, jsonString, expireTimestamp, false);
                    return ret;
                }
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：HASH， 命令：HEXIST key field
     *
     * @param key
     * @param field
     * @return
     */
    public boolean mapExist(String key, String field) {
        try {
            RMap<Object, Object> map = redissonClient.getMap(key);
            return Objects.nonNull(map) && map.isExists() && map.containsKey(field);
        } catch (Exception e) {
            log.error("helper error:", e);
            return false;
        }
    }

    /**
     * -- 元操作：数据结构：ZSET， 命令：ZADD key score1 member1
     * <p>
     * 往redis hash设置<field, value>键对
     *
     * @param keyEnum redis key
     * @param obj     zset obj
     * @param score   obj 分数
     * @return
     */
    public Object insertZSetByField(String keyEnum, Object obj, Long score, JSONWriter.Feature... features) {
        log.debug("insertZSetByField request:{}", keyEnum);
        try {
            final String k = key(keyEnum);
            RScoredSortedSet<Object> zset = redissonClient.getScoredSortedSet(k);
            //每次插入数据，刷新map30天缓存时间
            zset.expire(MAP_EXPIRE_DURATION);
            Object o = zset.add(score, JSON.toJSONString(obj, features));
            log.debug("insertZSetByField response:{}", o);
            return o;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：ZSET， 命令：ZADD myset 1 "member1" 2 "member2" 3 "member3"
     */
    public <FIELD> boolean batchInsertZSet(final String zsetKey, final Map<FIELD, Double> entries, JSONWriter.Feature... features) {
        log.debug("batchInsertZSet request:size={}", entries.size());
        if (CollectionUtils.isEmpty(entries)) {
            return true;
        }
        try {
            final String k = key(zsetKey);
            RScoredSortedSetAsync<FIELD> zset = redissonClient.getScoredSortedSet(k);
            zset.addAllAsync(entries);
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }

    public <V> Collection<V> zsetValueRange(String zKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        try {
            RScoredSortedSet<V> zset = redissonClient.getScoredSortedSet(key(zKey));
            return zset.valueRange(startScore, startScoreInclusive, endScore, endScoreInclusive);
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：ZSET， 命令：ZREMRANGEBYSCORE
     * <p>
     * 按score区间删除数据
     */
    public void cleanZSetByScoreAsync(String zsetKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        log.debug("insertZSetByField request:{}", zsetKey);
        try {
            final String k = key(zsetKey);
            RScoredSortedSet<Object> zset = redissonClient.getScoredSortedSet(k);
            //每次写zset，刷新map30天缓存时间
            zset.expire(MAP_EXPIRE_DURATION);
            zset.removeRangeByScoreAsync(startScore, startScoreInclusive, endScore, endScoreInclusive);
            log.debug("cleanZSetByScore end");
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
    }

    public List<String> batchQueryObjectByKey(Collection<String> keyList, JSONReader.Feature... features) {
        return batchQueryObjectByKey(keyList, String.class, features);
    }

    /**
     * -- 元操作：数据结构：String， 命令：BATCH GET key
     *
     * @return <T> T 按key顺序返回，未查得则为null
     */
    public <T> List<T> batchQueryObjectByKey(Collection<String> keyList, Class<T> clazz, JSONReader.Feature... features) {
        log.debug("batchQueryObjectByKey request:{}", keyList.size());
        if (CollectionUtils.isEmpty(keyList)) {
            return Collections.emptyList();
        }
        try {
            int size = keyList.size();
            //创建批量查询命令
//            List<RFuture<String>> rFutures = Lists.newArrayList();
            RBatch batch = redissonClient.createBatch();
            for (String s : keyList) {
                final String k = key(s);
                RFuture<String> async = batch.<String>getBucket(k).getAsync();
            }
            //批量查询
            BatchResult<?> execute = batch.execute();
            //解批量返回值
            List<T> retList = MyCollections.newArrayList();
            for (Object response : execute.getResponses()) {
                String resultStr = (String) response;
                if (StringUtils.isBlank(resultStr)) {
                    retList.add(null);
                } else {
                    T value = clazz == String.class ? (T) resultStr : JSONObject.parseObject(resultStr, clazz, features);
                    retList.add(value);
                }
            }

            log.debug("batchQueryObjectByKey response:{}", retList.size());
            return retList;
        } catch (Exception e) {
            log.error("helper error:", e);
        }
        return null;
    }

    /**
     * -- 元操作：数据结构：String， 命令：BATCH SET key value
     */
    public <T> boolean batchInsertObject(final Map<String, T> entries, final long timeout, JSONWriter.Feature... features) {
        log.debug("batchInsertObject request:size={}", entries.size());
        if (CollectionUtils.isEmpty(entries)) {
            return true;
        }
        try {
            //创建批量查询命令
            RBatch batch = redissonClient.createBatch();
            long timeoutRandom = getRandomDelay(timeout);
            entries.forEach((key, obj) -> {
                final String k = key(key);
                String value = toJSONString(obj, features);
                RBucketAsync<Object> bucket = batch.getBucket(k);
                bucket.setAsync(value, timeoutRandom, TimeUnit.SECONDS);
            });
            //批量查询
            batch.executeAsync();
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }

    /**
     * -- 元操作：数据结构：String， 命令：BATCH DEL key
     */
    public <T> boolean batchDeleteObjects(final Collection<String> keys) {
        log.debug("batchDeleteObjects request:size={}", keys.size());
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        try {
            //创建批量查询命令
            RBatch batch = redissonClient.createBatch();
            keys.forEach((key) -> {
                final String k = key(key);
                RBucketAsync<Object> bucket = batch.getBucket(k);
                bucket.deleteAsync();
            });
            //批量查询
            batch.executeAsync();
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }


    /**
     * -- 元操作：数据结构：ZSet， 命令：BATCH DEL key
     */
    public <T> boolean batchDeleteZSets(final Collection<String> keys) {
        log.debug("batchDeleteZSets request:size={}", keys.size());
        if (CollectionUtils.isEmpty(keys)) {
            return true;
        }
        try {
            //创建批量查询命令
            RBatch batch = redissonClient.createBatch();
            keys.forEach((key) -> {
                final String k = key(key);
                RScoredSortedSetAsync<Object> zset = batch.getScoredSortedSet(k);
                zset.deleteAsync();
            });
            //批量查询
            batch.executeAsync();
            return Boolean.TRUE;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 模糊删除
     *
     * @param prex 前缀
     */
    public void deleteByPrex(final String prex) {
        Iterable<String> keysByPattern = redissonClient.getKeys().getKeysByPattern(prex + "*");
        for (String s : keysByPattern) {
            redissonClient.getBucket(s).delete();
        }
    }

    /**
     * 锁
     *
     * @param key key
     */
    public boolean tryLock(String key, long lockTime) {
        return tryLock(key, 0, lockTime, TimeUnit.SECONDS);
    }

    /**
     * 锁
     *
     * @param key
     */
    public boolean tryLock(String key) {
        return tryLock(key, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 锁
     *
     * @param key
     */
    public boolean tryLock(String key, long waitTime, long lockTime, TimeUnit timeUnit) {
        log.debug(" key is {} isLock is {}", key);
        try {
            RLock rLock = redissonClient.getLock(key);
            Boolean isLock = rLock.tryLock(waitTime, lockTime, timeUnit);
            return isLock;
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
        return Boolean.FALSE;
    }

    /**
     * 解锁
     *
     * @param key
     */
    public void unLock(String key) {
        log.debug("unLock key is {}", key);
        try {
            RLock rLock = redissonClient.getLock(key);
            if (rLock != null && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        } catch (Exception e) {
            log.warn("helper error:", e);
        }
    }

    public <V> RBloomFilter<V> getBloomFilter(String key) {
        return redissonClient.getBloomFilter(key);
    }

    /**
     * 统一 helper key 名称
     *
     * @param key 名称
     * @return 新名称
     */
    private static String key(String key) {
        return key;
    }

    private String thumbsKey(String key) {
        return "THUMBS" + ":" + key;
    }

    /**
     * redis锁，双重校验工具
     *
     * @param checkCondition        校验方法：返回校验对象
     * @param lockKeyFunc           锁方法：返回Redis锁的key字符串
     * @param callbackWhileNotFound 校验对象不存在时，回调方法
     * @param callbackWhileFound    校验对象存在时，回调方法（入参为checkCondition的返回值）
     * @param timeoutFunction       tryLock取锁超时，回调方法
     * @param lockWaitTime          tryLock参数
     * @param lockTime              tryLock参数
     * @param timeUnit              tryLock参数
     * @param <T>                   checkCondition 查询对象的类型
     * @param <R>                   方法返回结果的类型
     * @return
     */
    public <T, R> R doubleCheckLock(Supplier<T> checkCondition,
                                    Supplier<String> lockKeyFunc,
                                    Supplier<R> callbackWhileNotFound,
                                    Function<T, R> callbackWhileFound,
                                    Supplier<R> timeoutFunction,
                                    long lockWaitTime,
                                    long lockTime,
                                    TimeUnit timeUnit) {
        T found = checkCondition.get();
        if (found == null) {
            String lockKey = lockKeyFunc.get();
            try {
                boolean getLock = RedisManager.get().tryLock(lockKey, lockWaitTime, lockTime, timeUnit);
                if (getLock) {
                    found = checkCondition.get();
                    if (found == null) {
                        return callbackWhileNotFound.get();
                    } else {
                        return callbackWhileFound.apply(found);
                    }
                }
                return timeoutFunction.get();
            } finally {
                RedisManager.get().unLock(lockKey);
            }
        } else {
            return callbackWhileFound.apply(found);
        }
    }

}
