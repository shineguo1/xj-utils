package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthGetTransactionReceiptReq extends BaseWeb3Cmd<EthGetTransactionReceipt, TransactionReceipt> implements BatchReq, Web3BatchCmd<EthGetTransactionReceipt, TransactionReceipt> {

    private final String transactionHash;

    public EthGetTransactionReceiptReq(String transactionHash) {
        this.transactionHash = transactionHash;
    }


    @Override
    public Request<?, EthGetTransactionReceipt> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        return web3j.ethGetTransactionReceipt(transactionHash);
    }

    @Override
    public TransactionReceipt getResult(EthGetTransactionReceipt response) {
        return response.getTransactionReceipt().orElse(null);
    }
}
