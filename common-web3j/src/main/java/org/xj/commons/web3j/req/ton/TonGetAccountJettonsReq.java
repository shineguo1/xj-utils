package org.xj.commons.web3j.req.ton;

import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.AccountJettonBalance;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.AccountJettonBalances;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonGetAccountJettons;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3Cmd;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 15:47
 */
public class TonGetAccountJettonsReq implements Web3Cmd<TonGetAccountJettons, AccountJettonBalances>, BatchReq {

    private String address;
    private Collection<String> currencies;

    public TonGetAccountJettonsReq(String account) {
        this.address = account;
        this.currencies = Collections.emptyList();
    }

    public TonGetAccountJettonsReq(String account, Collection<String> currencies) {
        this.address = account;
        this.currencies = currencies;
    }

    @Override
    public Request<?, TonGetAccountJettons> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetAccountJettons(address, String.join(",", currencies));
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public AccountJettonBalances getResult(TonGetAccountJettons response) {
        return response.getResult();
    }

    public List<AccountJettonBalance> getBalances(TonGetAccountJettons response) {
        return response.getResult() == null ? null : response.getResult().getBalances();
    }
}
