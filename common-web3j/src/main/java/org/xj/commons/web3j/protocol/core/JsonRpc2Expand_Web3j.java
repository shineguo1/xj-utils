package org.xj.commons.web3j.protocol.core;

import org.xj.commons.web3j.protocol.core.method.response.EthGetBlockReceipts;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.JsonRpc2_0Web3j;
import org.web3j.protocol.core.Request;

import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:24
 */
public class JsonRpc2Expand_Web3j extends JsonRpc2_0Web3j {

    public JsonRpc2Expand_Web3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpc2Expand_Web3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }


    public Request<?, EthGetBlockReceipts> ethGetBlockReceipts(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "eth_getBlockReceipts",
                Arrays.asList(defaultBlockParameter.getValue()),
                web3jService,
                EthGetBlockReceipts.class);
    }
}
