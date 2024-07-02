package org.xj.commons.web3j.protocol.core.decompile.cfx;

import lombok.Data;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/8/25 15:31
 */
@Data
public class CfxAddress {

    private String cfxAddress;
    private String evmAddress;
    private int[] hexValue;
    private String type;
    private Integer version;
}
