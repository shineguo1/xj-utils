package org.xj.commons.web3j.req;

import lombok.AllArgsConstructor;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/7/3 11:14
 */
public class EthSendRawTransactionReq implements BatchReq, Web3BatchCmd<EthSendTransaction, String> {

    private final String hexValue;


    @Override
    public Request<?, EthSendTransaction> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        return web3j.ethSendRawTransaction(hexValue);
    }

    @Override
    public String getResult(EthSendTransaction response) {
        return response.getTransactionHash();
    }

    // constructors
    // ===================================

    public EthSendRawTransactionReq(String hexValue) {
        this.hexValue = hexValue;
    }

    public static EthSendRawTransactionReq create(String hexValue) {
        return new EthSendRawTransactionReq(hexValue);
    }

    public static EthSendRawTransactionReq create(RawTransaction transaction, long chainId, Credentials credentials) {
        byte[] encode = TransactionEncoder.signMessage(transaction, chainId, credentials);
        String hexValue = Numeric.toHexString(encode);
        return create(hexValue);
    }

    public static EthSendRawTransactionReq create(RawTransaction transaction, long chainId, String walletPrivateKey) {
        return create(transaction, chainId, Credentials.create(walletPrivateKey));
    }

    public static Builder createUnsigned(BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit, String to, BigInteger value, Function function) {
        return new Builder(nonce, gasPrice, gasLimit, to, value, function);
    }


    /**
     * builder
     */
    @AllArgsConstructor
    public static class Builder {
        private BigInteger nonce;
        private BigInteger gasPrice;
        private BigInteger gasLimit;
        private String to;
        private BigInteger value;
        private Function function;

        public EthSendRawTransactionReq sign(long chainId, String walletPrivateKey) {
            return sign(chainId, Credentials.create(walletPrivateKey));
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
}
