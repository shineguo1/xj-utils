package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.xj.commons.web3j.Web3jUtil;
import org.xj.commons.web3j.abi.FunctionReturnDecoderPlus;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthCallReq extends BaseWeb3Cmd<EthCall, Object> implements BatchReq, Web3BatchCmd<EthCall, Object> {

    private final String address;
    private final String msgSender;
    private final Function function;
    private DefaultBlockParameter block;

    public EthCallReq(String address, Function function) {
        this.address = address;
        this.msgSender = null;
        this.function = function;
    }

    public EthCallReq(String address, String msgSender, Function function) {
        this.address = address;
        this.msgSender = msgSender;
        this.function = function;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public EthCallReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, EthCall> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        String encodedFunction = FunctionEncoder.encode(function);
        return web3j.ethCall(Transaction.createEthCallTransaction(msgSender, address, encodedFunction), block);
    }

    @Override
    public Object getResult(EthCall response) {
        Web3jUtil.validateResponse(response);
        List<Type> decode = FunctionReturnDecoderPlus.decode(response.getValue(), function.getOutputParameters());
        return Web3jUtil.convertTypeToValue(decode);
    }

}
