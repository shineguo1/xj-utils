package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.util.Objects;

/**
 * 网络请求等同于 EthGetBlockReq
 * 但是只返回 区块时间戳
 * 单位：秒
 *
 * @author xj
 * @version 1.0.0 createTime:  2023/2/23 10:01
 */
@Getter
public class EthGetBlockTimestampReq implements BatchReq, Web3BatchCmd<EthBlock, BigInteger> {

    private final EthGetBlockReq ethGetBlockReq;

    public EthGetBlockTimestampReq(BigInteger blockNumber) {
        this.ethGetBlockReq = new EthGetBlockReq(blockNumber).withFullTransactionObjects(false);
    }

    public EthGetBlockTimestampReq(DefaultBlockParameter block) {
        this.ethGetBlockReq = new EthGetBlockReq(block).withFullTransactionObjects(false);
    }

    public EthGetBlockTimestampReq(String blockHash) {
        this.ethGetBlockReq = new EthGetBlockReq(blockHash).withFullTransactionObjects(false);
    }

    @Override
    public Request<?, EthBlock> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        return ethGetBlockReq.getRequest(web3j, defaultBlock);
    }

    /**
     * @param response web3j的返回体
     * @return 区块时间戳，单位：秒
     */
    @Override
    public BigInteger getResult(EthBlock response) {
        return Objects.nonNull(response) && Objects.nonNull(response.getBlock()) ?
            response.getBlock().getTimestamp() : null;
    }
}
