package org.xj.commons.web3j.protocol.core.method.response.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @Date 2023/11/2 14:04
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TronAssetIssue {
    private String ownerAddress;
    private String name;
    private String abbr;
    private BigInteger totalSupply;
    private BigInteger trxNum;
    private BigInteger precision;
    private BigInteger num;
    private Long startTime;
    private Long endTime;
    private String description;
    private String url;
    private BigInteger freeAssetNetLimit;
    private BigInteger publicFreeAssetNetLimit;
    private String id;
}
