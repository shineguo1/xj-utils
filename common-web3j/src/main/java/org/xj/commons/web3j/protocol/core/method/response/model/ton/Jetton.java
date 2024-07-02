package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 14:50
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class Jetton {

    private boolean mintable;
    private BigInteger totalSupply;
    private JettonMeta metadata;
    private String verification;
    private BigInteger holdersCount;

}
