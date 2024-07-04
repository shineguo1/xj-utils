package org.xj.commons.web3j.req;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author xinjie_guo
 * @version 1.0.0 createTime:  2024/7/4 18:41
 */
public class EthGetTransactionCountReq implements BatchReq, Web3BatchCmd<EthGetTransactionCount, BigInteger> {

    private DefaultBlockParameter block;
    private String address;

    public EthGetTransactionCountReq(String address) {
        this.address = address;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public EthGetTransactionCountReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, EthGetTransactionCount> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        return web3j.ethGetTransactionCount(address, block);
    }

    @Override
    public BigInteger getResult(EthGetTransactionCount response) {
        return Objects.nonNull(response) ?
                response.getTransactionCount() : null;
    }
}
