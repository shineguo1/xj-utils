package org.xj.commons.web3j.req.starknet;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.xj.commons.web3j.protocol.core.JsonRpc_Starknet_Web3j;
import org.xj.commons.web3j.protocol.core.method.request.StrkFilter;
import org.xj.commons.web3j.protocol.core.method.response.model.starknet.StrkEvents;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetEvents;
import org.xj.commons.web3j.req.BaseWeb3Cmd;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3BatchCmd;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/12 9:53
 */
@Getter
public class StrkGetEventsReq extends BaseWeb3Cmd<StrkGetEvents, StrkEvents> implements BatchReq, Web3BatchCmd<StrkGetEvents, StrkEvents> {

    private final StrkFilter strkFilter;

    public StrkGetEventsReq(StrkFilter strkFilter) {
        this.strkFilter = strkFilter;
    }

    @Override
    public Request<?, StrkGetEvents> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Starknet_Web3j) {
            return ((JsonRpc_Starknet_Web3j) web3j).strkGetEvents(strkFilter);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TronGetAccountReq");
        }
    }

    @Override
    public StrkEvents getResult(StrkGetEvents response) {
        return response.getResult();
    }


}
