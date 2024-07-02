package org.xj.commons.web3j;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.req.BatchReq;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/18 17:36
 */
public abstract class AbstractMultiReqStrategy<T> implements BatchMergeStrategy<T> {
    protected T ret = null;
    private List<? extends BatchReq> reqList;
    private final int TOTAL_BATCH_TIMES = totalBatchTimes();
    private boolean isShutdown = false;

    protected abstract int totalBatchTimes();

    private AtomicInteger currentBatchTime = new AtomicInteger(0);

    @Override
    public boolean hasNext() {
        return !isShutdown && currentBatchTime.intValue() < TOTAL_BATCH_TIMES;
    }

    @Override
    public List<? extends BatchReq> nextReqList() {
        return this.reqList = (isShutdown ? Collections.emptyList() : createReqList(currentBatchTime.incrementAndGet()));
    }

    protected abstract List<? extends BatchReq> createReqList(Integer idx);

    @Override
    public List<? extends BatchReq> currentReqList() {
        return reqList != null ? reqList : Collections.emptyList();
    }

    @Override
    public void dealCurrentResults(List<Object> resList) {
        if (CollectionUtils.isEmpty(resList) && isInterruptWhenEmpty()) {
            shutdown();
            return;
        }
        dealResults(resList, currentBatchTime.intValue());
    }

    protected abstract void dealResults(List<Object> resList, Integer idx);

    @Override
    public T getResult() {
        return ret;
    }

    public void shutdown() {
        this.isShutdown = true;
    }

    protected boolean isInterruptWhenEmpty() {
        return true;
    }

}

