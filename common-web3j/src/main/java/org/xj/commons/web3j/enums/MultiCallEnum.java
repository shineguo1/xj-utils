package org.xj.commons.web3j.enums;

import org.xj.commons.toolkit.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/11/2 16:32
 */
@Getter
@AllArgsConstructor
public enum MultiCallEnum {

    /**
     * eth链
     */
    ETH(ChainTypeEnum.ETH.getCode(), "0x5BA1e12693Dc8F9c48aAD8770482f4739bEeD696", 12336033L),

    /**
     * Arbitrum链
     */
    ARB(ChainTypeEnum.ARB.getCode(), "0xca11bde05977b3631167028862be2a173976ca11", 7654707L),

    /**
     * Bsc链
     */
    BSC(ChainTypeEnum.BSC.getCode(), "0xca11bde05977b3631167028862be2a173976ca11", 15921452L),

    /**
     * Avax链
     */
    AVAX(ChainTypeEnum.AVAX.getCode(), "0xca11bde05977b3631167028862be2a173976ca11",11907934L ),
    /**
     * op链
     */
    OP(ChainTypeEnum.OP.getCode(), "0xca11bde05977b3631167028862be2a173976ca11",4286263L ),

    /**
     * Pol链
     */
    POL(ChainTypeEnum.Pol.getCode(),"0xca11bde05977b3631167028862be2a173976ca11",25770160L),

    /**
     * Canto链
     */
    CANTO(ChainTypeEnum.CANTO.getCode(),"0xca11bde05977b3631167028862be2a173976ca11",2905789L);

    /**
     * 链
     */
    private final String chain;
    /**
     * multiCall合约地址
     */
    private final String contract;
    /**
     * multiCall合约诞生区块
     */
    private final Long createdBlock;

    public static MultiCallEnum get(String chain) {
        if (StringUtils.isEmpty(chain)) {
            return null;
        }

        for (MultiCallEnum typeEnum : MultiCallEnum.values()) {
            if (typeEnum.getChain().equals(chain)) {
                return typeEnum;
            }
        }
        return null;
    }
}
