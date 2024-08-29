package org.xj.commons.web3j.req.ton;

import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.AccountNfts;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonGetAccountNfts;
import org.xj.commons.web3j.req.BaseWeb3Cmd;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3Cmd;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 15:47
 */
@Data
public class TonGetAccountNftsReq extends BaseWeb3Cmd<TonGetAccountNfts, AccountNfts> implements Web3Cmd<TonGetAccountNfts, AccountNfts>, BatchReq {

    @NotNull
    private String account;
    @Nullable
    private String collection;
    @Nullable
    private Integer limit;
    @Nullable
    private Integer offset;
    @Nullable
    private Boolean indirectOwnership;

    public TonGetAccountNftsReq(String address) {
        this.account = address;
    }

    public TonGetAccountNftsReq setCollection(@Nullable String collection) {
        this.collection = collection;
        return this;
    }

    public TonGetAccountNftsReq setLimit(@Nullable Integer limit) {
        this.limit = limit;
        return this;
    }

    public TonGetAccountNftsReq setOffset(@Nullable Integer offset) {
        this.offset = offset;
        return this;
    }

    public TonGetAccountNftsReq setIndirectOwnership(@Nullable Boolean indirectOwnership) {
        this.indirectOwnership = indirectOwnership;
        return this;
    }

    @Override
    public Request<?, TonGetAccountNfts> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetAccountNfts(account, collection, limit, offset, indirectOwnership);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public AccountNfts getResult(TonGetAccountNfts response) {
        return response.getResult();
    }
}
