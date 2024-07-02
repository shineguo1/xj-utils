package org.xj.commons.web3j.protocol.core.method.response.model;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @Date 2023/10/23
 */
@Data
public class TronAsset {

    private String key;

    private BigInteger value;

}
