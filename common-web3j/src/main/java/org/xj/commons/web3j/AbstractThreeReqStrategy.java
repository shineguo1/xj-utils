package org.xj.commons.web3j;

import org.xj.commons.web3j.req.BatchReq;

import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/18 17:36
 */
public abstract class AbstractThreeReqStrategy<T> extends AbstractMultiReqStrategy<T> {

    @Override
    public int totalBatchTimes() {
        return 3;
    }

    @Override
    protected List<? extends BatchReq> createReqList(Integer idx) {
        switch (idx) {
            case 1:
                return createReqList1();
            case 2:
                return createReqList2();
            case 3:
                return createReqList3();
            default:
                return Collections.emptyList();
        }
    }

    @Override
    protected void dealResults(List<Object> resList, Integer idx) {
        switch (idx) {
            case 1:
                dealResult1(resList);
                return;
            case 2:
                dealResult2(resList);
                return;
            case 3:
                dealResult3(resList);
                return;
            default:
        }
    }

    @Override
    public T getResult() {
        return ret;
    }

    protected abstract List<? extends BatchReq> createReqList1();

    protected abstract List<? extends BatchReq> createReqList2();

    protected abstract List<? extends BatchReq> createReqList3();

    protected abstract void dealResult1(List<Object> resList);

    protected abstract void dealResult2(List<Object> resList);

    protected abstract void dealResult3(List<Object> resList);
}

