package org.xj.commons.web3j.protocol.core.method.response.model.starknet;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/13 14:26
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StrkEvent {
    private String fromAddress;
    private List<String> keys;
    private List<String> data;
    private BigInteger blockNumber;
    private String blockHash;
    private String transactionHash;
}
