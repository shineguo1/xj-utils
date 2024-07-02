package org.xj.commons.web3j;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.enums.ChainTypeEnum;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3BatchCmd;
import org.xj.commons.web3j.req.Web3Cmd;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Response;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/6/30 18:21
 */
@Slf4j
public class BatchUtils {


    /* ====================== 多链单例 ==================== */

    /**
     * BatchUtils客户端集合，ChainTypeEnum.code -> 不同链 BatchUtils 实例
     */
    private static Map<String, BatchUtils> clientMap = Maps.newConcurrentMap();

    public static BatchUtils get(String chain) {
        String chainCode = ChainTypeEnum.explainCode(chain);
        if (clientMap.containsKey(chainCode)) {
            return clientMap.get(chainCode);
        } else {
            BatchUtils batchUtils = new BatchUtils(chainCode, false);
            clientMap.put(chainCode, batchUtils);
            return batchUtils;
        }
    }

    public static BatchUtils getHeavy(String chain) {
        String chainCode = ChainTypeEnum.explainCode(chain);
        String chainCodeHeavy = chainCode + "-heavy";
        if (clientMap.containsKey(chainCodeHeavy)) {
            return clientMap.get(chainCodeHeavy);
        } else {
            BatchUtils batchUtils = new BatchUtils(chainCode, true);
            clientMap.put(chainCodeHeavy, batchUtils);
            return batchUtils;
        }
    }



    /* ===================== 工具行为 =====================*/

    private Web3jUtil web3jUtil = null;
    private ThreadPoolExecutor asyncBatchPool;

    private final String chain;
    private final int limit;

    /**
     * 单次查链上限请求
     */
    public static final int EVERY_QUERY_LIMIT = 1000;

    private BatchUtils(String chain, boolean isHeavy) {
        this.chain = chain;
        this.limit = Web3jConfig.getBatchRequestLimitMap().getOrDefault(chain, EVERY_QUERY_LIMIT);
        if (isHeavy) {
            this.web3jUtil = Web3jUtil.getHeavy(chain);
        } else {
            this.web3jUtil = Web3jUtil.get(chain);
        }
        this.asyncBatchPool = new ThreadPoolExecutor(100, 500, 1, TimeUnit.SECONDS, new ArrayBlockingQueue<>(50000),
                new ThreadFactoryBuilder().setNameFormat("BatchUtilsThreadPool-" + chain + "-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public List<Object> batch(List<? extends BatchReq> reqList, Boolean requireAllSuccess, Long block, Feature... features) {
        return batch(reqList, requireAllSuccess, DefaultBlockParameter.valueOf(BigInteger.valueOf(block)), features);
    }

    /**
     * 使用协议批量查链, 每xx{@link this#EVERY_QUERY_LIMIT}个请求一组
     * <p>
     * requireAllSuccess = true 时，任意一条请求失败，返回null。
     * 例如：查询 uint(成功，value=1)，uint(失败), uint(成功，value=2)。 返回：null。
     * <p>
     * requireAllSuccess = false 时，失败的请求在返回list中用null表示。
     * 例如：查询 uint(成功，value=1)，uint(失败), uint(成功，value=2)。 返回：[1, null, 2]。
     *
     * @return 查询结果
     */
    public List<Object> batch(List<? extends BatchReq> reqList, Boolean requireAllSuccess, DefaultBlockParameter block, Feature... features) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        List<Web3BatchCmd<? extends Response<?>, ?>> web3BatchReqList = Lists.newArrayList();
        List<Web3Cmd<? extends Response<?>, ?>> threadPoolBatchReqList = Lists.newArrayList();
        //2023-03-03 multiCall合约组装1000个方法，返回报文太大，decode非常慢，所以全部改用web3Batch
        //2023-12-25 非evm链的api存在许多常规的http接口，不兼容evm的json-rpc的batchRequest，所以直接用线程池并发请求。
        for (BatchReq req : reqList) {
            if (req instanceof Web3BatchCmd) {
                web3BatchReqList.add((Web3BatchCmd) req);
            } else {
                threadPoolBatchReqList.add((Web3Cmd) req);
            }
        }
        long featureMasks = Feature.features(features);
        List<Object> web3BatchResults = web3Batch(web3BatchReqList, requireAllSuccess, block, featureMasks);
        List<Object> threadPoolBatchResults = threadPoolCall(threadPoolBatchReqList, requireAllSuccess, block, featureMasks);
        //返回结果数量与请求参数数量不一致，直接返回null，防止遍历坐标时出现数组越界错误。
        if (Objects.isNull(web3BatchResults) || web3BatchReqList.size() != web3BatchResults.size()) {
            return null;
        }
        if (Objects.isNull(threadPoolBatchResults) || threadPoolBatchReqList.size() != threadPoolBatchResults.size()) {
            return null;
        }
        List<Object> results = mergeResults(reqList, web3BatchResults, threadPoolBatchResults);
        if (requireAllSuccess) {
            if (results.contains(null)) {
                results = null;
            }
        }
        return results;
    }

    private List<Object> mergeResults(List<? extends BatchReq> reqList, List<Object> web3BatchResults, List<Object> threadPoolBatchResults) {
        if (CollectionUtils.isEmpty(threadPoolBatchResults)) {
            return web3BatchResults;
        } else if (CollectionUtils.isEmpty(web3BatchResults)) {
            return threadPoolBatchResults;
        } else {
            List<Object> ret = Lists.newArrayList();
            int index1 = 0, index2 = 0;
            for (BatchReq req : reqList) {
                if (req instanceof Web3BatchCmd) {
                    ret.add(web3BatchResults.get(index1++));
                } else {
                    ret.add(threadPoolBatchResults.get(index2++));
                }
            }
            return ret;
        }
    }

    private List<Object> threadPoolCall(List<Web3Cmd<? extends Response<?>, ?>> reqList, Boolean requireAllSuccess, DefaultBlockParameter block, long featureMasks) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        List<Object> ret;
        boolean isSync = (featureMasks & BatchMergeStrategy.Feature.BatchExecutionModeSync.mask) != 0;
        if (isSync) {
            ret = CollectionUtils.toStream(reqList).map(req -> web3jUtil.call(req, block)).collect(Collectors.toList());
        } else {
            ret = callAsync(reqList, block);
        }
        if (requireAllSuccess) {
            //如果需要全部成功，则返回结果里不能有null
            return ret.size() != reqList.size() || ret.contains(null) ? null : ret;
        } else {
            //如果允许部分成功，则返回结果允许有null，只需判断请求和响应的长度是否相等。
            return ret.size() != reqList.size() ? null : ret;
        }
    }

    @NotNull
    private List<Object> callAsync(List<Web3Cmd<? extends Response<?>, ?>> reqList, DefaultBlockParameter block) {
        List<Object> ret;
        ret = Lists.newArrayList();
        List<FutureTask<Object>> futures = reqList.stream().map(req -> new FutureTask<Object>(() -> web3jUtil.call(req, block))).collect(Collectors.toList());
        futures.forEach(futureTask -> asyncBatchPool.submit(futureTask));
        for (FutureTask<Object> futureTask : futures) {
            try {
                CollectionUtils.addIf(ret, futureTask.get(), true);
            } catch (Exception e) {
                log.error("batchUtils 线程池报错，e:" + Throwables.getStackTraceAsString(e));
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    /**
     * 使用web3j的batchRequest api实现调用（对节点来说是处理多个请求）
     */
    private List<Object> web3Batch(List<? extends Web3BatchCmd<? extends Response<?>, ?>> reqList, Boolean requireAllSuccess, DefaultBlockParameter block, long featureMasks) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        List<Object> ret;
        if (reqList.size() <= limit) {
            ret = web3jUtil.callBatch(reqList, block);
        } else {
            ret = Lists.newArrayList();
            //拆分, 分组查询
            List<? extends List<? extends Web3BatchCmd<? extends Response<?>, ?>>> splits = CollectionUtils.split(reqList, limit);
            boolean isSync = (featureMasks & BatchMergeStrategy.Feature.BatchExecutionModeSync.mask) != 0;
            if (isSync) {
                ret = callBatchSync(block, splits);
            } else {
                ret = callBatchAsync(block, splits);
            }
        }
        if (requireAllSuccess) {
            //如果需要全部成功，则返回结果里不能有null
            return ret.size() != reqList.size() || ret.contains(null) ? null : ret;
        } else {
            //如果允许部分成功，则返回结果允许有null，只需判断请求和响应的长度是否相等。
            return ret.size() != reqList.size() ? null : ret;
        }
    }

    @NotNull
    private List<Object> callBatchSync(DefaultBlockParameter block, List<? extends List<? extends Web3BatchCmd<? extends Response<?>, ?>>> splits) {
        List<Object> ret = Lists.newArrayList();
        CollectionUtils.toStream(splits).forEach(
                subReqList -> {
                    List<Object> objects = web3jUtil.callBatch(subReqList, block);
                    CollectionUtils.addAll(ret, objects);
                }
        );
        return ret;
    }

    @NotNull
    private List<Object> callBatchAsync(DefaultBlockParameter block, List<? extends List<? extends Web3BatchCmd<? extends Response<?>, ?>>> splits) {
        List<Object> ret = Lists.newArrayList();
        List<FutureTask<List<Object>>> futures = splits.stream().map(subReqList -> new FutureTask<>(() -> {
            return web3jUtil.callBatch(subReqList, block);
        })).collect(Collectors.toList());
        futures.forEach(futureTask -> asyncBatchPool.submit(futureTask));
        for (FutureTask<List<Object>> futureTask : futures) {
            try {
                CollectionUtils.addAll(ret, futureTask.get());
            } catch (Exception e) {
                log.error("batchUtils 线程池报错，e:" + Throwables.getStackTraceAsString(e));
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    public enum Feature {
        /**
         * 特性：超过batchSize时，用顺序的方式执行
         */
        BatchExecutionModeSync(1 << 1),
        /**
         * 备用feature，用以提醒mask的编写格式
         */
        Feature2(1 << 2);

        final int mask;

        Feature(int mask) {
            this.mask = mask;
        }

        static long features(Feature... features) {
            long feature = 0;
            for (Feature f : features) {
                feature |= f.mask;
            }
            return feature;
        }
    }
}