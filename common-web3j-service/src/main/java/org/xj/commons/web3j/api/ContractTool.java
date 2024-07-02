package org.xj.commons.web3j.api;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.toolkit.LRUCache;
import org.xj.commons.web3j.config.ServiceConfig;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 14:48
 */
@Slf4j
public class ContractTool {

    private static final LRUCache<String, Set<String>> byteCodsCache = new LRUCache<>(1000);

    /**
     * 合约和代理合约的 methodCode
     * 开销：(代理合约层数+2)次查链
     *
     * @param chain    链
     * @param contract 合约
     * @param block    区块
     * @return
     */
    public static Set<String> getFullMethodCodes(String chain, String contract, DefaultBlockParameter block) {
        String redisKey = "BLOCK_CHAIN:CONTRACT_METHOD_CODES:" + chain + ":" + contract;
        Set<String> ret = null;
        try {
            //1. 查内存
            if (byteCodsCache.containsKey(redisKey)) {
                return ret = byteCodsCache.get(redisKey);
            }
            //2. 查redis
            if (ServiceConfig.isUseRedis()) {
                List<String> redisRes = ServiceConfig.getRedisManager().queryListByKey(redisKey, String.class);
                if (CollectionUtils.isNotEmpty(redisRes)) {
                    return ret = new HashSet<>(redisRes);
                }
            }
            //3. 查链
            ret = InternalContractApi.getFullMethodCodes(chain, contract, block);
            return ret;
        } finally {
            if (CollectionUtils.isNotEmpty(ret)) {
                byteCodsCache.put(redisKey, ret);
                if (ServiceConfig.isUseRedis()) {
                    ServiceConfig.getRedisManager().insertObject(redisKey, ret, TimeUnit.MINUTES.toSeconds(3));
                }
            }
        }
    }


}
