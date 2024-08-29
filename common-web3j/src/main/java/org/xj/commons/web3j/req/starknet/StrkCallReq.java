package org.xj.commons.web3j.req.starknet;

import lombok.Getter;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.xj.commons.toolkit.BigIntegerUtils;
import org.xj.commons.web3j.Web3jUtil;
import org.xj.commons.web3j.abi.StrkEncoder;
import org.xj.commons.web3j.abi.StrkTypeDecoder;
import org.xj.commons.web3j.protocol.core.JsonRpc_Starknet_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkCall;
import org.xj.commons.web3j.req.BaseWeb3Cmd;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3BatchCmd;

import java.math.BigInteger;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/12 9:53
 */
@Getter
public class StrkCallReq extends BaseWeb3Cmd<StrkCall, Object> implements BatchReq, Web3BatchCmd<StrkCall, Object> {

    /**
     * contract_address
     */
    private final String address;
    private final Function function;
    private DefaultBlockParameter block;

    public StrkCallReq(String address, Function function) {
        this.address = address;
        this.function = function;
    }

    /**
     * @param block 优先级大于{@link org.xj.commons.web3j.BatchUtils#batch}参数里的block
     */
    public StrkCallReq withBlock(DefaultBlockParameter block) {
        this.block = block;
        return this;
    }

    @Override
    public Request<?, StrkCall> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        if (web3j instanceof JsonRpc_Starknet_Web3j) {
            List<Object> inputs = function.getInputParameters().stream().flatMap(o -> {
                if (StrkTypeDecoder.isUint256(o.getClass())) {
                    //uint256分成低32位hex和高32位hex放入请求参数。
                    BigInteger u256 = (BigInteger) Web3jUtil.getTypeValue(o);
                    BigInteger highBit = u256.shiftRight(32 * 4);
                    BigInteger lowBit = u256.and(BigIntegerUtils.decode("0xffffffffffffffffffffffffffffffff"));
                    return Stream.of(lowBit, highBit);
                } else {
                    return Stream.of(Web3jUtil.getTypeValue(o));
                }
            }).collect(Collectors.toList());
            return ((JsonRpc_Starknet_Web3j) web3j).strkCall(address, StrkEncoder.buildMethodSelector(function), inputs, block);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TronGetAccountReq");
        }
    }

    @Override
    public Object getResult(StrkCall response) {
        //static struct怎么处理？
        Web3jUtil.validateResponse(response);
        List<String> result = response.getResult();
        List<TypeReference<Type>> outputParameters = function.getOutputParameters();
        List<Type> decodeList = StrkTypeDecoder.decode(result, outputParameters);
        return Web3jUtil.convertTypeToValue(decodeList);
    }


}
