package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/23 10:01
 */
@Getter
public class EthGetBlockReq implements BatchReq, Web3BatchCmd<EthBlock, EthBlock.Block> {

    private DefaultBlockParameter block;
    private String blockHash;
    private Boolean returnFullTransactionObjects = false;

    public EthGetBlockReq(BigInteger blockNumber) {
        if (Objects.isNull(blockNumber)) {
            throw new InvalidParameterException("blockNumber cannot be null");
        }
        this.block = DefaultBlockParameter.valueOf(blockNumber);
    }

    public EthGetBlockReq(DefaultBlockParameter block) {
        this.block = block;
    }

    public EthGetBlockReq withFullTransactionObjects(Boolean b) {
        this.returnFullTransactionObjects = b;
        return this;
    }

    public EthGetBlockReq(String blockHash) {
        this.blockHash = blockHash;
    }

    @Override
    public Request<?, EthBlock> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (Objects.nonNull(block)) {
            return web3j.ethGetBlockByNumber(block, returnFullTransactionObjects);
        } else if (Objects.nonNull(blockHash)) {
            return web3j.ethGetBlockByHash(blockHash, returnFullTransactionObjects);
        } else {
            throw new InvalidParameterException("block or hash cannot be all null");
        }

    }

    @Override
    public EthBlock.Block getResult(EthBlock response) {
        return Objects.nonNull(response) ?
                response.getBlock() : null;
    }
}
