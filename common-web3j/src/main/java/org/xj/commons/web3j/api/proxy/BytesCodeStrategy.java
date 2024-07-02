package org.xj.commons.web3j.api.proxy;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.AbstractOneReqStrategy;
import org.xj.commons.web3j.enums.ProxyTypeEnum;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.EthGetCodeReq;
import org.web3j.utils.Numeric;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 14:22
 */
public class BytesCodeStrategy extends AbstractOneReqStrategy<String> {
    private static final String PATTERN = "(0x)?363d3d373d3d3d363d73(.){40}5af43d82803e903d91602b57fd5bf3";

    private final String contractAddress;

    public BytesCodeStrategy(String contractAddress) {
        this.contractAddress = contractAddress;
    }
    @Override
    protected List<? extends BatchReq> createReqList() {
        return Collections.singletonList(new EthGetCodeReq(contractAddress));
    }

    @Override
    protected void dealResults(List<Object> resList) {
        if (CollectionUtils.isEmpty(resList) || resList.get(0) == null) {
            return;
        }
        String byteCode = (String) resList.get(0);
        boolean isMatch = Pattern.matches(PATTERN, byteCode.toLowerCase());
        ret = isMatch ? ProxyTypeEnum.formatAddress("0x" + Numeric.cleanHexPrefix(byteCode).substring(20, 20 + 40)) : null;
    }
}
