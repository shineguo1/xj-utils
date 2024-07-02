package org.xj.commons.web3j.config;

import org.xj.commons.redis.client.IRedisManager;
import org.xj.commons.redis.client.RedisManager;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/11/9 16:52
 */
public class ServiceConfig {

    @Setter
    @Getter
    private static boolean useRedis = true;

    @Setter
    @Getter
    private static IRedisManager redisManager = RedisManager.get();

}
