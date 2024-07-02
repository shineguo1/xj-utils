package org.xj.commons.web3j;

import org.xj.commons.web3j.req.BatchReq;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/18 17:36
 */
public abstract class AbstractOneReqStrategy<T> implements BatchMergeStrategy<T> {
    protected T ret = null;
    private List<? extends BatchReq> reqList;
    private static final int TOTAL_BATCH_TIMES = 1;
    private AtomicInteger currentBatchTime = new AtomicInteger(0);

    @Override
    public boolean hasNext() {
        return currentBatchTime.intValue() < TOTAL_BATCH_TIMES;
    }

    @Override
    public List<? extends BatchReq> nextReqList() {
        switch (currentBatchTime.incrementAndGet()) {
            case 1:
                return this.reqList = createReqList();
            default:
                return this.reqList = Collections.emptyList();
        }
    }

    protected abstract List<? extends BatchReq> createReqList();


    @Override
    public List<? extends BatchReq> currentReqList() {
        return reqList != null ? reqList : Collections.emptyList();
    }

    @Override
    public void dealCurrentResults(List<Object> resList) {
        if (currentBatchTime.intValue() == 1) {
            dealResults(resList);
        }
    }

    protected abstract void dealResults(List<Object> resList);

    @Override
    public T getResult() {
        return ret;
    }
}

