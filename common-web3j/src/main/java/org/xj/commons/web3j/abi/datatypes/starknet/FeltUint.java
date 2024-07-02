package org.xj.commons.web3j.abi.datatypes.starknet;

import org.xj.commons.web3j.abi.datatypes.Uint252;

import java.math.BigInteger;

/**
 * @deprecated 使用通用的 {@link Uint252}
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 15:23
 */
class FeltUint extends Uint252 {
    public FeltUint(BigInteger value) {
        super(value);
    }
}
