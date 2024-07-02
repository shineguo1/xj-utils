package org.xj.commons.web3j.abi.datatypes;

import org.web3j.abi.datatypes.Address;
import org.web3j.utils.Numeric;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/12 16:05
 */
public class Address252 extends Address {

    public Address252(String hexValue) {
        super(256, hexValue);
    }

    @Override
    public String toString() {
        return Numeric.toHexStringWithPrefixZeroPadded(toUint().getValue(), 63);
    }

}
