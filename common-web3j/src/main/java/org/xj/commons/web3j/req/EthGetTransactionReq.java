package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthTransaction;
import org.web3j.protocol.core.methods.response.Transaction;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthGetTransactionReq implements BatchReq, Web3BatchCmd<EthTransaction, Transaction>{

    private final String transactionHash;

    public EthGetTransactionReq(String transactionHash){
        this.transactionHash = transactionHash;
    }


    @Override
    public Request<?, EthTransaction> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock){
        return web3j.ethGetTransactionByHash(transactionHash);
    }

    @Override
    public Transaction getResult(EthTransaction response) {
        return response.getTransaction().orElse(null);
    }
}
