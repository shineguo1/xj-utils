package org.xj.commons.web3j.req;

import org.xj.commons.web3j.protocol.core.JsonRpc_Tron_Web3j;
import org.xj.commons.web3j.protocol.core.TronRequest;
import org.xj.commons.web3j.protocol.core.method.response.TronGetAccount;
import org.xj.commons.web3j.protocol.core.method.response.model.TronAccount;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/10/24 15:48
 */
public class TronGetAccountReq implements Web3Cmd<TronGetAccount, TronAccount> {

    private String address;
    private boolean visible;

    public TronGetAccountReq(String address, boolean visible) {
        this.address = address;
        this.visible = visible;
    }

    @Override
    public TronRequest<?, TronGetAccount> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Tron_Web3j) {
            return ((JsonRpc_Tron_Web3j) web3j).tronGetAccount(address, visible);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TronGetAccountReq");
        }
    }

    @Override
    public TronAccount getResult(TronGetAccount response) {
        return response.getResult();
    }
}
