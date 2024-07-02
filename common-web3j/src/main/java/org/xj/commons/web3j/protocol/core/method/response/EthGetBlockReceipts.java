package org.xj.commons.web3j.protocol.core.method.response;

import org.web3j.protocol.core.Response;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:28
 */
public class EthGetBlockReceipts extends Response<List<TransactionReceipt>> {
    public List<TransactionReceipt> getTransactionReceipts() {
        return getResult();
    }
}
