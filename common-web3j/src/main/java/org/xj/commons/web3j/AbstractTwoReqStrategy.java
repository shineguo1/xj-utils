package org.xj.commons.web3j;

import org.xj.commons.web3j.req.BatchReq;

import java.util.Collections;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author xj
 * @Date 2023/1/5
 * @ProjectName IntelliJ IDEA
 * @Version 1.0.0
 */
public abstract class AbstractTwoReqStrategy<T> extends AbstractMultiReqStrategy<T> {

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
            default:
        }
    }

    @Override
    public T getResult() {
        return ret;
    }

    protected abstract List<? extends BatchReq> createReqList1();

    protected abstract List<? extends BatchReq> createReqList2();

    protected abstract void dealResult1(List<Object> resList);

    protected abstract void dealResult2(List<Object> resList);

}
