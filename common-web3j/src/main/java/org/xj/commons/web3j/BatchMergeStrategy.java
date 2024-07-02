package org.xj.commons.web3j;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.sideoutput.SideOutputFunction;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Web3j批量查询-合并查询策略
 *
 * @param <T> 返回结果类型
 * @author xj
 * @version 1.0.0 createTime:  2022/9/28 11:19
 */
public interface BatchMergeStrategy<T> extends SideOutputFunction {

    Logger LOGGER = LoggerFactory.getLogger(BatchMergeStrategy.class);

    /**
     * 是否还有下一个web3j批量请求
     *
     * @return TRUE OR FALSE
     */
    boolean hasNext();

    /**
     * 返回下一次批量查询请求
     *
     * @return 请求列表
     */
    @NotNull
    List<? extends BatchReq> nextReqList();

    /**
     * 返回当前批量查询请求
     *
     * @return 请求列表
     */
    @NotNull
    List<? extends BatchReq> currentReqList();

    /**
     * 处理当前批次批量查询结果
     */
    void dealCurrentResults(List<Object> resList);

    /**
     * 返回最终处理结果
     *
     * @return
     */
    T getResult();

    /**
     * 功能：将不同 batchStrategy 的查链请求进行合并，减少整体查链次数，同时保持解耦
     * <p>
     * 对每个策略来说：
     * 1. 按顺序查询 nextReqList 组织请求参数
     * 2. 按顺序回调 dealCurrentResults 处理查询结果
     * 3. 当 hasNext = false终止
     * <p>
     * 对整体来说：
     * 1. 按顺序合并每个解析策略的请求
     * 2. 合并查询结果拆分，并回调给每个策略的解析方法
     * 3. 当所有解析策略都没有下一个请求(hasNext)时终止
     *
     * @param chain           链
     * @param batchStrategies 需要合并请求的解析策略
     */
    static void execute(String chain, Collection<? extends BatchMergeStrategy> batchStrategies, DefaultBlockParameter block, Feature... features) {
        if (CollectionUtils.isEmpty(batchStrategies)) {
            return;
        }
        Collection<? extends BatchMergeStrategy> nextBatchStrategies = filterNextStrategies(batchStrategies);
        long featureMasks = Feature.features(features);
        boolean ignoreException = (featureMasks & Feature.IgnoreException.mask) != 0;
        BatchUtils.Feature[] utilsFeatures = Feature.toUtilsFeatures(featureMasks);

        while (CollectionUtils.isNotEmpty(nextBatchStrategies)) {
            //【一、组合请求】
            List<? extends BatchReq> reqList = Lists.newArrayList();
            nextBatchStrategies.forEach(strategy -> CollectionUtils.addAll(reqList, strategy.nextReqList()));
            //【二、发送请求】
            BatchUtils batchUtils = BatchUtils.get(chain);
            List<Object> resList = batchUtils.batch(reqList, false, block, utilsFeatures);
            if (Objects.isNull(resList)) {
                throw new RuntimeException("查链失败");
            }
            //【三、解析结果】
            AtomicInteger index = new AtomicInteger(0);
            for (BatchMergeStrategy strategy : nextBatchStrategies) {
                int start = index.get();
                int end = index.addAndGet(strategy.currentReqList().size());
                try {
                    strategy.dealCurrentResults(resList.subList(start, end));
                } catch (Exception e) {
                    if (!ignoreException) {
                        throw e;
                    } else {
                        LOGGER.error(Throwables.getStackTraceAsString(e));
                    }
                }
            }
            //【四、过滤出还有下一个请求的策略】
            nextBatchStrategies = filterNextStrategies(batchStrategies);
        }
    }

    /**
     * 过滤出有下一个请求的策略
     */
    static List<? extends BatchMergeStrategy> filterNextStrategies(Collection<? extends BatchMergeStrategy> batchStrategies) {
        if (CollectionUtils.isEmpty(batchStrategies)) {
            return Collections.emptyList();
        }
        return batchStrategies.stream().filter(BatchMergeStrategy::hasNext).collect(Collectors.toList());
    }

    enum Feature {
        /**
         * 特性：忽视dealResult中抛出的异常
         */
        IgnoreException(1),
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

        static BatchUtils.Feature[] toUtilsFeatures(long featureMasks) {
            boolean isSync = (featureMasks & Feature.BatchExecutionModeSync.mask) != 0;
            List<BatchUtils.Feature> utilsFeatures = Lists.newArrayList();
            if (isSync) {
                utilsFeatures.add(BatchUtils.Feature.BatchExecutionModeSync);
            }
            return utilsFeatures.toArray(new BatchUtils.Feature[0]);
        }
    }

}
