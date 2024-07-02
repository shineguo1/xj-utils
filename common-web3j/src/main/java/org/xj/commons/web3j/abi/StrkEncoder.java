package org.xj.commons.web3j.abi;

import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.abi.datatypes.MethodIdFunction;
import org.xj.commons.web3j.event.core.EventName;
import org.web3j.abi.datatypes.Function;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/13 15:27
 */
public class StrkEncoder {
    private final static BigInteger MASK_250 = BigInteger.valueOf(2).pow(250).subtract(BigInteger.ONE);

    public static String buildMethodSelector(Function function) {
        if (function instanceof MethodIdFunction) {
            return ((MethodIdFunction) function).getMethodId();
        }
        //starknet selector，仅对方法名加密
        String name = function.getName();
        return buildSignature(name);
    }

    public static String buildEventSignature(EventName eventAnnotation) {
        if (Objects.nonNull(eventAnnotation) && StringUtils.isNotEmpty(eventAnnotation.signature())) {
            return eventAnnotation.signature();
        } else if (StringUtils.isNotEmpty(eventAnnotation.value())) {
            return buildSignature(eventAnnotation.value());
        } else {
            throw new RuntimeException("无法确定signature");
        }
    }

    public static String buildSignature(String name) {
        //starknet_entry_point_selector = (keccak256(method_name) & (2^250 -1)).toString(16)
        //starknet_event_key0 = (keccak256(event_name) & (2^250 -1)).toString(16)
        return "0x" + keccak256(name).and(MASK_250).toString(16);
    }

    /**
     * keccak256运算
     */
    public static BigInteger keccak256(String msg) {
        return Numeric.toBigInt(Hash.sha3(msg.getBytes(StandardCharsets.UTF_8)));
    }
}
