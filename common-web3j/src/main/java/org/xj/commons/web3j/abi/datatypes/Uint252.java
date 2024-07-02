package org.xj.commons.web3j.abi.datatypes;

import org.web3j.abi.datatypes.Uint;

import java.math.BigInteger;

/**
 * cairo最小数据单元为felt252位。实际以256位存储，形式上标记为Uint252以示区别。
 *
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 15:27
 */
public class Uint252 extends Uint {
    public Uint252(BigInteger value) {
        super(256, value);
    }
}
