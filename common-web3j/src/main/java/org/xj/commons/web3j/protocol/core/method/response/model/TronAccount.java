package org.xj.commons.web3j.protocol.core.method.response.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * 类上使用蛇型注解，非蛇形字段用别名标记
 *
 * @author xj
 * @Date 2023/10/23
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TronAccount {
    private String accountName;
    private String address;
    private Long createTime;
    private Long netWindowSize;
    private Boolean netWindowOptimized;
    private BigInteger balance;

    private List<TronAsset> asset;
    /**
     * 类上使用蛇型注解，非蛇形字段用别名标记
     */
    @JsonAlias("assetV2")
    private List<TronAsset> assetV2;
    /**
     * 类上使用蛇型注解，非蛇形字段用别名标记
     */
    @JsonAlias("free_asset_net_usageV2")
    private List<TronAsset> freeAssetNetUsageV2;

    private List<TronVote> votes;

}
