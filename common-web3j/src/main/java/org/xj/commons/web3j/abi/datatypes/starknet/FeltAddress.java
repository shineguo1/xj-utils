package org.xj.commons.web3j.abi.datatypes.starknet;

import org.xj.commons.web3j.abi.datatypes.Address252;

/**
 * @deprecated 使用通用的 {@link Address252}
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 15:22
 */
@Deprecated
class FeltAddress extends Address252 {
    public FeltAddress(String hexValue) {
        super(hexValue);
    }
}
