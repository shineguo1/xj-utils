package org.xj.commons.web3j.req;

import lombok.Getter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:37
 */
@Getter
public class EthEstimateGasReq extends BaseWeb3Cmd<EthEstimateGas, BigInteger> implements BatchReq, Web3BatchCmd<EthEstimateGas, BigInteger> {

    private final Transaction transaction;

    public EthEstimateGasReq(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public Request<?, EthEstimateGas> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        return web3j.ethEstimateGas(transaction);
    }

    @Override
    public BigInteger getResult(EthEstimateGas response) {
        if (response.hasError()) {
            return null;
        } else {
            return response.getAmountUsed();
        }
    }
}
