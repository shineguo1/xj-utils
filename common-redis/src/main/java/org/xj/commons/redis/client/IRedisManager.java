package org.xj.commons.redis.client;

import com.alibaba.fastjson2.JSONReader;
import com.alibaba.fastjson2.JSONWriter;
import com.alibaba.fastjson2.TypeReference;
import org.redisson.api.RBloomFilter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * redis服务
 *
 * @author xj
 * @version 1.0.0 createTime: 2016/4/15
 */
public interface IRedisManager {


    /**
     * 1、查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @param clazz   指定返回List内存放的对象类型
     * @param <T>     返回对象类型,集合泛型
     * @return List<T>返回对象集合
     */
     <T> List<T> queryListByKey(final String keyEnum, final Class<T> clazz, JSONReader.Feature... features);

    /**
     * 查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @return <T> T
     */
    @SuppressWarnings("DuplicatedCode")
     <T> T queryObjectByKey(String keyEnum, Class<T> clazz, JSONReader.Feature... features);

    /**
     * 查询redis String结构
     *
     * @param keyEnum 查询关键字
     * @return <T> T
     */
    @SuppressWarnings("DuplicatedCode")
     <T> T queryObjectByKey(String keyEnum, TypeReference<T> typeReference, JSONReader.Feature... features);

    /**
     * -- 元操作：数据结构：String， 命令：DEL key
     * 删除
     *
     * @param keyEnum
     * @return
     */
     Boolean deleteObjectByKey(String keyEnum);

    /**
     * 2、插入redis 数据库
     *
     * @param keyEnum 关键字
     * @param obj     保存对象
     * @return 对象类型, 泛型
     */
     boolean insertObject(final String keyEnum, final Object obj);

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
     boolean insertObject(final String keyEnum, final Object obj, final long timeout, JSONWriter.Feature... features);

    /**
     * -- 元操作：数据结构：String， 命令：EXISTS KEY
     * <p>
     * 3、插入redis 数据库,设置有效期
     *
     * @param keyEnum 关键字
     * @return 对象类型, 泛型
     */
     boolean exist(final String keyEnum);

    /**
     * -- 元操作：数据结构：String， 命令：GET KEY  返回：JsonString
     * <p>
     * 6、查询redis 数据库
     *
     * @param keyEnum 查询关键字
     * @return String
     */
     String queryObjectByKey(String keyEnum);

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
     Object insertMapByField(String keyEnum, String field, Object value, Long timeout, JSONWriter.Feature... features);

    /**
     * -- 元操作：数据结构：HASH， 命令：DEL KEY
     */
     boolean deleteMap(String keyEnum);

    /**
     * -- 元操作：数据结构：HASH， 命令：HGET KEY FIELD
     */
     String queryMapByFiled(String keyEnum, String field) ;

    /**
     * -- 元操作：数据结构：HASH， 命令：HGET KEY FIELD
     */
     <T> T queryMapByFiled(String keyEnum, String field, TypeReference<T> typeReference);

    /**
     * -- 元操作：数据结构：HASH， 命令：HEXIST key field
     *
     * @param key
     * @param field
     * @return
     */
     boolean mapExist(String key, String field);
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
     Object insertZSetByField(String keyEnum, Object obj, Long score, JSONWriter.Feature... features) ;

    /**
     * -- 元操作：数据结构：ZSET， 命令：ZADD myset 1 "member1" 2 "member2" 3 "member3"
     */
     <FIELD> boolean batchInsertZSet(final String zsetKey, final Map<FIELD, Double> entries, JSONWriter.Feature... features) ;

     <V> Collection<V> zsetValueRange(String zKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive);

    /**
     * -- 元操作：数据结构：ZSET， 命令：ZREMRANGEBYSCORE
     * <p>
     * 按score区间删除数据
     */
     void cleanZSetByScoreAsync(String zsetKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) ;

     List<String> batchQueryObjectByKey(Collection<String> keyList, JSONReader.Feature... features) ;

    /**
     * -- 元操作：数据结构：String， 命令：BATCH GET key
     *
     * @return <T> T 按key顺序返回，未查得则为null
     */
     <T> List<T> batchQueryObjectByKey(Collection<String> keyList, Class<T> clazz, JSONReader.Feature... features) ;

    /**
     * -- 元操作：数据结构：String， 命令：BATCH SET key value
     */
     <T> boolean batchInsertObject(final Map<String, T> entries, final long timeout, JSONWriter.Feature... features) ;

    /**
     * -- 元操作：数据结构：String， 命令：BATCH DEL key
     */
     <T> boolean batchDeleteObjects(final Collection<String> keys);

    /**
     * 模糊删除
     *
     * @param prex 前缀
     */
     void deleteByPrex(final String prex);

    /**
     * 锁
     *
     * @param key key
     */
     boolean tryLock(String key, long lockTime);

    /**
     * 锁
     *
     * @param key
     */
     boolean tryLock(String key);

    /**
     * 锁
     *
     * @param key
     */
     boolean tryLock(String key, long waitTime, long lockTime, TimeUnit timeUnit);

    /**
     * 解锁
     *
     * @param key
     */
     void unLock(String key);

     <V> RBloomFilter<V> getBloomFilter(String key);


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
     <T, R> R doubleCheckLock(Supplier<T> checkCondition,
                                    Supplier<String> lockKeyFunc,
                                    Supplier<R> callbackWhileNotFound,
                                    Function<T, R> callbackWhileFound,
                                    Supplier<R> timeoutFunction,
                                    long lockWaitTime,
                                    long lockTime,
                                    TimeUnit timeUnit);

}
