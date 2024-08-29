package org.xj.commons.web3j.req.ton;

import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.Jetton;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonGetJetton;
import org.xj.commons.web3j.req.BaseWeb3Cmd;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3Cmd;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 15:47
 */
public class TonGetJettonReq extends BaseWeb3Cmd<TonGetJetton, Jetton> implements Web3Cmd<TonGetJetton, Jetton>, BatchReq {

    private String address;

    public TonGetJettonReq(String address) {
        this.address = address;
    }

    @Override
    public Request<?, TonGetJetton> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetJetton(address);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public Jetton getResult(TonGetJetton response) {
        return response.getResult();
    }
}
