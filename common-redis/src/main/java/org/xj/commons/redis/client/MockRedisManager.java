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
 * @author xj
 * @version 1.0.0 createTime:  2023/11/9 16:44
 */
public class MockRedisManager implements  IRedisManager{
    @Override
    public <T> List<T> queryListByKey(String keyEnum, Class<T> clazz, JSONReader.Feature... features) {
        return null;
    }

    @Override
    public <T> T queryObjectByKey(String keyEnum, Class<T> clazz, JSONReader.Feature... features) {
        return null;
    }

    @Override
    public <T> T queryObjectByKey(String keyEnum, TypeReference<T> typeReference, JSONReader.Feature... features) {
        return null;
    }

    @Override
    public Boolean deleteObjectByKey(String keyEnum) {
        return null;
    }

    @Override
    public boolean insertObject(String keyEnum, Object obj) {
        return false;
    }

    @Override
    public boolean insertObject(String keyEnum, Object obj, long timeout, JSONWriter.Feature... features) {
        return false;
    }

    @Override
    public boolean exist(String keyEnum) {
        return false;
    }

    @Override
    public String queryObjectByKey(String keyEnum) {
        return null;
    }

    @Override
    public Object insertMapByField(String keyEnum, String field, Object value, Long timeout, JSONWriter.Feature... features) {
        return null;
    }

    @Override
    public boolean deleteMap(String keyEnum) {
        return false;
    }

    @Override
    public String queryMapByFiled(String keyEnum, String field) {
        return null;
    }

    @Override
    public <T> T queryMapByFiled(String keyEnum, String field, TypeReference<T> typeReference) {
        return null;
    }

    @Override
    public boolean mapExist(String key, String field) {
        return false;
    }

    @Override
    public Object insertZSetByField(String keyEnum, Object obj, Long score, JSONWriter.Feature... features) {
        return null;
    }

    @Override
    public <FIELD> boolean batchInsertZSet(String zsetKey, Map<FIELD, Double> entries, JSONWriter.Feature... features) {
        return false;
    }

    @Override
    public <V> Collection<V> zsetValueRange(String zKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {
        return null;
    }

    @Override
    public void cleanZSetByScoreAsync(String zsetKey, double startScore, boolean startScoreInclusive, double endScore, boolean endScoreInclusive) {

    }

    @Override
    public List<String> batchQueryObjectByKey(Collection<String> keyList, JSONReader.Feature... features) {
        return null;
    }

    @Override
    public <T> List<T> batchQueryObjectByKey(Collection<String> keyList, Class<T> clazz, JSONReader.Feature... features) {
        return null;
    }

    @Override
    public <T> boolean batchInsertObject(Map<String, T> entries, long timeout, JSONWriter.Feature... features) {
        return false;
    }

    @Override
    public <T> boolean batchDeleteObjects(Collection<String> keys) {
        return false;
    }

    @Override
    public void deleteByPrex(String prex) {

    }

    @Override
    public boolean tryLock(String key, long lockTime) {
        return false;
    }

    @Override
    public boolean tryLock(String key) {
        return false;
    }

    @Override
    public boolean tryLock(String key, long waitTime, long lockTime, TimeUnit timeUnit) {
        return false;
    }

    @Override
    public void unLock(String key) {

    }

    @Override
    public <V> RBloomFilter<V> getBloomFilter(String key) {
        return null;
    }

    @Override
    public <T, R> R doubleCheckLock(Supplier<T> checkCondition, Supplier<String> lockKeyFunc, Supplier<R> callbackWhileNotFound, Function<T, R> callbackWhileFound, Supplier<R> timeoutFunction, long lockWaitTime, long lockTime, TimeUnit timeUnit) {
        return null;
    }
}
