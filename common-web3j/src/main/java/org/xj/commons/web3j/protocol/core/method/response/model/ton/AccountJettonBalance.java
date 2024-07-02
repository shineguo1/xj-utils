package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 16:47
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class AccountJettonBalance {
    private BigInteger balance;
    private Wallet walletAddress;
    private JettonMeta jetton;
    private Price price;

}
