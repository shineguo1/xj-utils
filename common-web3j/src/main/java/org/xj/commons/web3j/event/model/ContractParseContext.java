package org.xj.commons.web3j.event.model;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.event.core.BaseEventBiz;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.web3j.protocol.core.methods.response.Log;

import java.math.BigInteger;
import java.util.List;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/8 11:09
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContractParseContext<T, R> {

    private String chain;
    private Log log;
    private T event;
    private BaseEventBiz parseBean;

    private R parseResult;

    public ContractParseContext(String chain, Log log, T event, BaseEventBiz parseBean) {
        this.chain = chain;
        this.log = log;
        this.event = event;
        this.parseBean = parseBean;
    }

    public BigInteger getBlockNumber() {
        return Objects.nonNull(log) ? log.getBlockNumber() : null;
    }

    public String getContractAddress() {
        return Objects.nonNull(log) ? log.getAddress() : null;
    }

    public String getTransactionHash() {
        return Objects.nonNull(log) ? log.getTransactionHash() : null;
    }

    public String getSignature() {
        List<String> topics;
        if (Objects.isNull(log) || CollectionUtils.isEmpty(topics = log.getTopics())) {
            return null;
        }
        return topics.get(0);
    }

}
