package org.xj.commons.web3j.req.ton;

import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.NftCollection;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonGetNftCollection;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3Cmd;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 15:47
 */
public class TonGetNftCollectionReq implements Web3Cmd<TonGetNftCollection, NftCollection>, BatchReq {

    private String address;

    public TonGetNftCollectionReq(String address) {
        this.address = address;
    }

    @Override
    public Request<?, TonGetNftCollection> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetNftCollection(address);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public NftCollection getResult(TonGetNftCollection response) {
        return response.getResult();
    }
}
