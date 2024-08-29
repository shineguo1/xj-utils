package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthGetBalanceReq extends BaseWeb3Cmd<EthGetBalance, BigInteger> implements BatchReq, Web3BatchCmd<EthGetBalance, BigInteger> {

    private final String address;
    private DefaultBlockParameter block;

    public EthGetBalanceReq(String address) {
        this.address = address;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public EthGetBalanceReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, EthGetBalance> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        return web3j.ethGetBalance(address, block);
    }

    @Override
    public BigInteger getResult(EthGetBalance response) {
        return response.getBalance();
    }
}
