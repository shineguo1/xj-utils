package org.xj.commons.web3j.protocol.core;

import org.xj.commons.web3j.protocol.core.method.request.StrkFilter;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkCall;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetBlockWithTxHashes;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetEvents;
import com.google.common.collect.ImmutableMap;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:24
 */
public class JsonRpc_Starknet_Web3j extends JsonRpc2_0Web3j {

    public JsonRpc_Starknet_Web3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpc_Starknet_Web3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }


    public Request<?, StrkCall> strkCall(String contractAddress,
                                         String entryPointSelector,
                                         List<?> calldata,
                                         DefaultBlockParameter block) {
        return new Request<>(
                "starknet_call",
                Arrays.asList(ImmutableMap.of("contract_address", contractAddress,
                                "entry_point_selector", entryPointSelector,
                                "calldata", calldata),
                        toBlock(block)),
                web3jService,
                StrkCall.class);
    }

    /**
     * web3j原生HttpService不支持。
     * 需要搭配StrkHttpService才能适用，这个类中做了接口适配的特殊处理。
     *
     * @see org.xj.commons.web3j.protocol.http.StarknetHttpService
     */
    @Override
    public Request<?, EthLog> ethGetLogs(
            org.web3j.protocol.core.methods.request.EthFilter ethFilter) {
        return super.ethGetLogs(ethFilter);
    }

    /**
     * 传递 DefaultBlockParameter 类型而非 String 类型。便于 {@link org.xj.commons.web3j.protocol.http.StarknetHttpService} 桥接
     */
    @Override
    public Request<?, EthBlock> ethGetBlockByNumber(DefaultBlockParameter defaultBlockParameter, boolean returnFullTransactionObjects) {
        return new Request("eth_getBlockByNumber", Arrays.asList(defaultBlockParameter, returnFullTransactionObjects), this.web3jService, EthBlock.class);
    }


    public Request<?, StrkGetEvents> strkGetEvents(
            StrkFilter filter) {
        return new Request<>("starknet_getEvents", Arrays.asList(filter), web3jService, StrkGetEvents.class);
    }

    public Request<?, StrkGetBlockWithTxHashes> strkGetBlockWithTxHashes(DefaultBlockParameter blockParameter) {
        return new Request<>("starknet_getBlockWithTxHashes", Arrays.asList(toBlock(blockParameter)), web3jService, StrkGetBlockWithTxHashes.class);
    }

    /**
     * 转成starknet接受的block格式
     */
    public Object toBlock(DefaultBlockParameter blockParameter) {
        if (blockParameter instanceof DefaultBlockParameterNumber) {
            return ImmutableMap.of("block_number", ((DefaultBlockParameterNumber) blockParameter).getBlockNumber());
        } else {
            return blockParameter.getValue();
        }
    }
}
