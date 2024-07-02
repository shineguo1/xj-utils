package org.xj.commons.web3j.protocol.core.method.response.model.starknet;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/13 14:07
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StrkBlock<TRANSACTION> {

    private String status;
    private String blockHash;
    private String parentHash;
    private BigInteger blockNumber;
    private String newRoot;
    private BigInteger timestamp;
    private String sequencerAddress;
    private GasPrice l1GasPrice;
    private GasPrice l1DataGasPrice;
    private String l1DaMode;
    private String starknetVersion;
    private List<TRANSACTION> transactions;

    @Data
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class GasPrice {
        private String priceInFri;
        private String priceInWei;
    }
}
