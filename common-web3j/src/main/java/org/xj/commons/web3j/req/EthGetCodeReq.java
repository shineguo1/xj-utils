package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetCode;

import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/23 10:01
 */
@Getter
public class EthGetCodeReq implements BatchReq, Web3BatchCmd<EthGetCode, String> {

    private DefaultBlockParameter block;
    private final String address;

    public EthGetCodeReq(String address) {
        this.address = address;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public EthGetCodeReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, EthGetCode> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        return web3j.ethGetCode(address, block);
    }

    @Override
    public String getResult(EthGetCode response) {
        return Objects.nonNull(response) ?
            response.getCode() : null;
    }
}
