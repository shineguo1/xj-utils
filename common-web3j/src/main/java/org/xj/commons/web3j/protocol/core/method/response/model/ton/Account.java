package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 16:47
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Account {
    private String address;
    private BigInteger balance;
    private BigInteger lastActivity;
    private String status;
    private List<String> interfaces;
    private String name;
    private Boolean isScam;
    private String icon;
    private Boolean memoRequired;
    private List<String> getMethods;
    private Boolean isSuspended;
    private Boolean isWallet;
}
