package org.xj.commons.web3j.enums;

import org.xj.commons.web3j.BatchMergeStrategy;
import org.xj.commons.web3j.api.proxy.BytesCodeStrategy;
import org.xj.commons.web3j.api.proxy.MethodStrategy;
import org.xj.commons.web3j.api.proxy.SlotStrategy;
import lombok.AllArgsConstructor;
import org.web3j.abi.datatypes.Address;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * 代理协议类型
 *
 * @author xj
 * @version 1.0.0 createTime:  2022/6/13 15:31
 */
@AllArgsConstructor
public enum ProxyTypeEnum {
    /**
     * https://eips.ethereum.org/EIPS/eip-897
     */
    EIP_897(null) {
        /**
         * 调用合约implementation()方法
         */
        @Override
        public BatchMergeStrategy<String> proxyAddressStrategy(String contractAddress) {
            return new MethodStrategy(contractAddress);
        }
    },

    /**
     * https://eips.ethereum.org/EIPS/eip-1822
     */
    EIP_1822(keccak256("PROXIABLE")),

    /**
     * https://eips.ethereum.org/EIPS/eip-1967
     * 备注：如果使用 EIP_1967_Beacon 槽位，则应为空
     */
    EIP_1967_Logic(keccak256("eip1967.proxy.implementation").subtract(BigInteger.ONE)),
    /**
     * https://eips.ethereum.org/EIPS/eip-1967
     * 备注：EIP_1967_Logic 为空时，考虑使用这个槽位。
     */
    EIP_1967_Beacon(keccak256("eip1967.proxy.beacon").subtract(BigInteger.ONE)),

    /**
     * OpenZeppelin's Unstructured Storage proxy pattern.
     */
    OpenZeppelin(keccak256("org.zeppelinos.proxy.implementation")),

    /**
     * https://eips.ethereum.org/EIPS/eip-1167
     */
    EIP_1167(null) {
        /**
         * 调用合约implementation()方法
         */
        @Override
        public BatchMergeStrategy<String> proxyAddressStrategy(String contractAddress) {
            return new BytesCodeStrategy(contractAddress);
        }
    };

    /**
     * 槽位。许多代理协议都约定把代理合约地址存储在某个特定槽位中
     */
    private BigInteger slot;

    /**
     * 根据当前代理协议类型，查询代理地址
     */
    public static String formatAddress(String address) {
        try {
            BigInteger num = Numeric.toBigInt(address);
            if (num.longValue() == 0 || num.longValue() == 1) {
                //有些合约存在function ()能接受任何未定义的方法，返回值一般为true或者false
                return null;
            }
            return new Address(address).getValue();
        } catch (Exception e) {
            //若返回地址是0x或其他，不能转换成BigInteger，这些情况都说明 internalProxyAddress 方法返回的不是代理合约地址。输入合约不是遵从这个代理协议的。
            return null;
        }
    }

    public BatchMergeStrategy<String> proxyAddressStrategy(String contractAddress) {
        return new SlotStrategy(contractAddress, slot);
    }

    /**
     * 返回16进制槽位
     */
    public String slotToHexString() {
        return Objects.isNull(slot) ? null : Numeric.toHexStringWithPrefix(slot);
    }

    /**
     * keccak256运算
     */
    private static BigInteger keccak256(String msg) {
        return Numeric.toBigInt(Hash.sha3(msg.getBytes(StandardCharsets.UTF_8)));
    }

}
