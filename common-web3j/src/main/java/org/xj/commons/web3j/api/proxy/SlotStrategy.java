package org.xj.commons.web3j.api.proxy;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.AbstractOneReqStrategy;
import org.xj.commons.web3j.enums.ProxyTypeEnum;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.EthGetStorageAtReq;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 14:22
 */
public class SlotStrategy extends AbstractOneReqStrategy<String> {

    private String address;
    private BigInteger slot;

    public SlotStrategy(String address, BigInteger slot) {
        this.address = address;
        this.slot = slot;
    }

    @Override
    protected List<? extends BatchReq> createReqList() {
        return Collections.singletonList(new EthGetStorageAtReq(address, slot));
    }

    @Override
    protected void dealResults(List<Object> resList) {
        if (CollectionUtils.isEmpty(resList) || resList.get(0) == null) {
            return;
        }
        ret = ProxyTypeEnum.formatAddress((String) resList.get(0));
    }
}
