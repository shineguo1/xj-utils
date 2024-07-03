package org.xj.commons.web3j.req;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/7/3 13:54
 */
public class EthCallSendTransactionReq {

    private String to;
    private Function function;
    private BigInteger value;
    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;

    public EthCallSendTransactionReq(BigInteger nonce,
                                     BigInteger gasPrice,
                                     BigInteger gasLimit,
                                     String to,
                                     BigInteger value,
                                     Function function) {
        this.to = to;
        this.function = function;
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        this.value = value;
    }

    public EthCallSendTransactionReq(BigInteger nonce,
                                     BigInteger gasPrice,
                                     BigInteger gasLimit,
                                     String to,
                                     BigInteger value) {
        this(nonce, gasPrice, gasLimit, to, value, null);
    }

    public EthCallSendTransactionReq(BigInteger nonce,
                                     BigInteger gasPrice,
                                     BigInteger gasLimit,
                                     String to,
                                     Function function) {
        this(nonce, gasPrice, gasLimit, to, null, function);
    }

    public EthSendRawTransactionReq sign(long chainId, Credentials credentials) {
        String encodeFuncData = null;
        if (function != null) {
            encodeFuncData = FunctionEncoder.encode(function);
        }
        RawTransaction transaction = RawTransaction.createTransaction(
                nonce,
                gasPrice,
                gasLimit,
                to,
                value,
                encodeFuncData
        );
        return EthSendRawTransactionReq.create(transaction, chainId, credentials);
    }


}
