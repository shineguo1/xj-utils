package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetStorageAt;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthGetStorageAtReq implements BatchReq, Web3BatchCmd<EthGetStorageAt, String> {

    private final String contractAddress;
    private final BigInteger slot;

    private DefaultBlockParameter block;

    public EthGetStorageAtReq(String contractAddress, BigInteger slot) {
        this.contractAddress = contractAddress;
        this.slot = slot;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public EthGetStorageAtReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, EthGetStorageAt> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        return web3j.ethGetStorageAt(contractAddress, slot, block);
    }

    @Override
    public String getResult(EthGetStorageAt response) {
        return response.getData();
    }
}
