package org.xj.commons.web3j.req.ton;

import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.Account;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonGetAccount;
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
public class TonGetAccountReq extends BaseWeb3Cmd<TonGetAccount, Account> implements Web3Cmd<TonGetAccount, Account>, BatchReq {

    private String address;

    public TonGetAccountReq(String account) {
        this.address = account;
    }

    @Override
    public Request<?, TonGetAccount> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetAccount(address);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public Account getResult(TonGetAccount response) {
        return response.getResult();
    }

}
