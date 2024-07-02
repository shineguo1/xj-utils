package org.xj.commons.toolkit;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/11/2 15:58
 */
public class BigIntegerUtils {


    /**
     * 解码8进制、10进制、16进制字符串数
     * -- copy and modify from {@link Long#decode(String)}
     */
    public static BigInteger decode(String nm) throws NumberFormatException {
        int radix = 10;
        int index = 0;
        boolean negative = false;
        BigInteger result;

        if (nm.length() == 0)
            throw new NumberFormatException("Zero length string");
        char firstChar = nm.charAt(0);
        // Handle sign, if present
        if (firstChar == '-') {
            negative = true;
            index++;
        } else if (firstChar == '+')
            index++;

        // Handle radix specifier, if present
        if (nm.startsWith("0x", index) || nm.startsWith("0X", index)) {
            index += 2;
            radix = 16;
        } else if (nm.startsWith("#", index)) {
            index++;
            radix = 16;
        } else if (nm.startsWith("0", index) && nm.length() > 1 + index) {
            index++;
            radix = 8;
        }

        if (nm.startsWith("-", index) || nm.startsWith("+", index))
            throw new NumberFormatException("Sign character in wrong position");

        try {
            result = new BigInteger(nm.substring(index), radix);
            result = negative ? result.negate() : result;
        } catch (NumberFormatException e) {
            // If number is Long.MIN_VALUE, we'll end up here. The next line
            // handles this case, and causes any genuine format error to be
            // rethrown.
            String constant = negative ? ("-" + nm.substring(index))
                    : nm.substring(index);
            result = new BigInteger(constant, radix);
        }
        return result;
    }

    public static BigInteger defaultZero(BigInteger num) {
        return ObjectUtils.defaultIfNull(num, BigInteger.ZERO);
    }

    /**
     * nums累加，default 0
     */
    public static BigInteger add(BigInteger... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigInteger.ZERO;
        }
        BigInteger sum = BigInteger.ZERO;
        for (BigInteger num : nums) {
            sum = sum.add(Objects.isNull(num) ? BigInteger.ZERO : num);
        }
        return sum;
    }


    /**
     * nums累减，default 0
     */
    public static BigInteger subtract(BigInteger... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigInteger.ZERO;
        }
        BigInteger diff = nums[0];
        for (int i = 1; i < nums.length; i++) {
            diff = diff.subtract(Objects.isNull(nums[i]) ? BigInteger.ZERO : nums[i]);
        }
        return diff;
    }

    /**
     * nums累乘，default 0
     */
    public static BigInteger multiply(BigInteger... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigInteger.ZERO;
        }
        BigInteger product = BigInteger.ONE;
        for (BigInteger num : nums) {
            product = product.multiply(Objects.isNull(num) ? BigInteger.ZERO : num);
        }
        return product;
    }

    public static BigInteger max(BigInteger a, BigInteger b) {
        if (a.compareTo(b) > 0) return a;
        else return b;
    }

    public static BigInteger min(BigInteger a, BigInteger b) {
        if (a.compareTo(b) < 0) return a;
        else return b;
    }

    public static void main(String[] args) {
        System.out.println(max(BigInteger.TEN, BigInteger.ZERO));
        System.out.println(max(BigInteger.ZERO, BigInteger.ZERO));
        System.out.println(max(BigInteger.valueOf(-1L), BigInteger.ZERO));
    }

}