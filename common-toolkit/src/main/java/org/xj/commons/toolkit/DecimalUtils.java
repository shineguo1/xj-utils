package org.xj.commons.toolkit;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2021/11/9 11:02
 * @description
 */
public class DecimalUtils {

    private static final ThreadLocal<DecimalFormat> df1 = ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));

    /* ======================================== 数值运算 ====================================== */

    /**
     * nums累加，default 0
     */
    public static BigDecimal add(Number... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = BigDecimal.ZERO;
        for (Number num : nums) {
            sum = sum.add(Objects.isNull(num) ? BigDecimal.ZERO : toBigDecimal(num));
        }
        return sum;
    }

    /**
     * nums累减，default 0
     */
    public static BigDecimal subtract(Number... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal diff = ObjectUtils.defaultIfNull(toBigDecimal(nums[0]), BigDecimal.ZERO);
        for (int i = 1; i < nums.length; i++) {
            diff = diff.subtract(Objects.isNull(nums[i]) ? BigDecimal.ZERO : toBigDecimal(nums[i]));
        }
        return diff;
    }

    /**
     * nums累乘，default 0
     */
    public static BigDecimal multiply(Number... nums) {
        if (Objects.isNull(nums) || nums.length == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal product = BigDecimal.ONE;
        for (Number num : nums) {
            product = product.multiply(Objects.isNull(num) ? BigDecimal.ZERO : toBigDecimal(num));
        }
        return product;
    }

    /**
     * a➗b，default 0 (a == null 或 b == null 或 b == 0， 返回 0)
     */
    public static BigDecimal divide(Number a, Number b, int scale, RoundingMode roundingMode) {
        BigDecimal _a = Objects.isNull(a) ? BigDecimal.ZERO : toBigDecimal(a);
        BigDecimal _b = Objects.isNull(b) ? BigDecimal.ZERO : toBigDecimal(b);
        if (_b.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return _a.divide(_b, scale, roundingMode);
    }

    public static BigDecimal doubleDivide(Number a, Number b) {
        double _a = Objects.isNull(a) ? 0d : a.doubleValue();
        double _b = Objects.isNull(b) ? 0d : b.doubleValue();
        if (equalZero(toBigDecimal(b))) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.valueOf(_a / _b);
    }

    /**
     * a➗b，default 0 (a == null 或 b == null 或 b == 0， 返回 0， 保留18位小数)
     */
    public static BigDecimal divide18(Number a, Number b) {
        return divide(a, b, 18, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(Number a, Number b) {
        if (b == null) {
            return BigDecimal.ZERO;
        }
        int bScale = String.valueOf(b.longValue()).length();
        int scale = Math.max(bScale, 18);
        return divide(a, b, scale, RoundingMode.HALF_UP);
    }

    /**
     * value/(10 ** ((decimals0 + decimals1)/2))
     */
    public static BigDecimal calcLiquidity(Number value, Integer decimals0, Integer decimals1) {
        double avgDecimals = (decimals0 + decimals1) / 2.0;
        return divide18(value, BigDecimal.valueOf(Math.pow(10, avgDecimals)));
    }

    /**
     * 小数部分保留 value.scale + decimals 位
     *
     * @see DecimalUtils#rightShiftDecimal(Number, Integer, Integer)
     */
    public static BigDecimal rightShiftDecimal(Number value, Integer decimals) {
        decimals = ObjectUtils.defaultIfNull(decimals, 0);
        BigDecimal bigDecimalValue = toBigDecimal(value);
        int scale;
        if (bigDecimalValue == null) {
            scale = decimals;
        } else {
            bigDecimalValue = bigDecimalValue.stripTrailingZeros();
            scale = bigDecimalValue.scale() + decimals;
        }
        return rightShiftDecimal(bigDecimalValue, decimals, scale);
    }

    /**
     * 小数部分保留 18 位
     *
     * @see DecimalUtils#rightShiftDecimal(Number, Integer, Integer)
     */
    public static BigDecimal rightShiftDecimal18(Number value, Integer decimals) {
        return rightShiftDecimal(value, decimals, 18);
    }

    /**
     * 十进制的右移位运算，即小数点左移 decimals 位
     * 小数部分保留 scale 位
     * value /(10 ** decimals)
     */
    public static BigDecimal rightShiftDecimal(Number value, Integer decimals, Integer scale) {
        decimals = ObjectUtils.defaultIfNull(decimals, 0);
        return divide(value, BigDecimal.valueOf(Math.pow(10, decimals)), scale, RoundingMode.HALF_UP);
    }

    /**
     * 十进制的左移位运算，即小数点右移 decimals 位
     * decimals为负时，小数部分保留默认位数
     * value * (10 ** decimals)
     */
    public static BigDecimal leftShiftDecimal(Number value, Integer decimals) {
        decimals = ObjectUtils.defaultIfNull(decimals, 0);
        return multiply(value, BigDecimal.valueOf(Math.pow(10, decimals)));
    }

    public static BigDecimal abs(BigDecimal value) {
        if (value == null) {
            return null;
        } else {
            return value.multiply(BigDecimal.valueOf(value.compareTo(BigDecimal.ZERO)));
        }

    }

    /* ======================================== 比较大小 ====================================== */

    public static <T extends Number & Comparable<T>> boolean gt(T a, T b) {
        return Objects.nonNull(a) && Objects.nonNull(b) && a.compareTo(b) > 0;
    }

    public static <T extends Number & Comparable<T>> boolean gte(T a, T b) {
        return Objects.nonNull(a) && Objects.nonNull(b) && a.compareTo(b) >= 0;
    }

    public static <T extends Number & Comparable<T>> boolean lt(T a, T b) {
        return Objects.nonNull(a) && Objects.nonNull(b) && a.compareTo(b) < 0;
    }

    public static <T extends Number & Comparable<T>> boolean lte(T a, T b) {
        return Objects.nonNull(a) && Objects.nonNull(b) && a.compareTo(b) <= 0;
    }

    public static <T extends Number & Comparable<T>> boolean eq(T a, T b) {
        return Objects.nonNull(a) && Objects.nonNull(b) && a.compareTo(b) == 0;
    }

    public static boolean equalZero(BigDecimal a) {
        return eq(a, BigDecimal.ZERO);
    }

    public static boolean equalZeroOrNull(BigDecimal a) {
        return a == null || eq(a, BigDecimal.ZERO);
    }

    public static boolean equalZero(BigInteger a) {
        return eq(a, BigInteger.ZERO);
    }

    public static boolean equalZeroOrNull(BigInteger a) {
        return a == null || eq(a, BigInteger.ZERO);
    }

    public static boolean equalZeroOrNull(Number a) {
        if (a == null) {
            return true;
        } else if (a instanceof BigInteger) {
            return equalZero((BigInteger) a);
        } else {
            return equalZero(toBigDecimal(a));
        }
    }

    public static boolean allEqZeroOrNull(BigInteger... a) {
        return Arrays.stream(a).allMatch(DecimalUtils::equalZeroOrNull);
    }

    public static boolean allEqZeroOrNull(Number... numbers) {
        return Arrays.stream(numbers).allMatch(DecimalUtils::equalZeroOrNull);
    }

    /* ======================================== 转换 ====================================== */

    public static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) {
            return (BigDecimal) num;
        } else if (num instanceof Integer || num instanceof Long) {
            return BigDecimal.valueOf(num.longValue());
        } else if (num instanceof Float || num instanceof Double) {
            return BigDecimal.valueOf(num.doubleValue());
        } else if (num instanceof BigInteger) {
            return new BigDecimal((BigInteger) num);
        } else if (num == null) {
            return null;
        } else {
            throw new IllegalArgumentException(num.getClass() + "不能转成BigDecimal, num:" + num);
        }
    }

    /**
     * 限制最大有效位数，避免fastJson2序列化时丢失正负号
     *
     * @param value 数据
     * @return
     */
    public static BigDecimal limit(BigDecimal value) {
        return Objects.nonNull(value) ? value.round(new MathContext(30)) : null;
    }

    /**
     * 是否都小于等于零
     *
     * @param numbers number
     * @return boolean
     */
    public static boolean lteZeroAll(BigDecimal... numbers) {
        for (BigDecimal number : numbers) {
            if (DecimalUtils.gt(number, BigDecimal.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否都小于等于零 (null当作零)
     *
     * @param numbers number
     * @return boolean
     */
    public static boolean lteZeroAll(BigInteger... numbers) {
        for (BigInteger number : numbers) {
            if (DecimalUtils.gt(number, BigInteger.ZERO)) {
                return false;
            }
        }
        return true;
    }

    public static boolean lteZeroArrays(List<BigInteger> numbers) {
        for (BigInteger number : numbers) {
            if (DecimalUtils.gt(toBigDecimal(number), BigDecimal.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否都小于等于零
     *
     * @param numbers number
     * @return boolean
     */
    public static boolean lteZeroAll(Collection<? extends Number> numbers) {
        for (Number number : numbers) {
            if (DecimalUtils.gt(toBigDecimal(number), BigDecimal.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否都小于等于零
     *
     * @param numbers number
     * @return boolean
     */
    public static boolean lteZeroAll(Number... numbers) {
        for (Number number : numbers) {
            if (DecimalUtils.gt(toBigDecimal(number), BigDecimal.ZERO)) {
                return false;
            }
        }
        return true;
    }

    public static BigDecimal objToDecimal(Object obj) {
        BigDecimal result = BigDecimal.ZERO;
        if (Objects.nonNull(obj)) {
            result = new BigDecimal(obj.toString());
        }
        return result;
    }

    public static BigInteger objToBigInteger(Object obj) {
        if (Objects.nonNull(obj)) {
            String str = obj.toString();
            if (obj instanceof BigDecimal) {
                return new BigDecimal(str).toBigInteger();
            }
            return new BigInteger(str);
        }
        return BigInteger.ZERO;
    }

    public static BigDecimal convertTokenToDecimals(BigInteger value, Integer decimal) {
        if (decimal == 0 || BigInteger.ZERO.compareTo(value) >= 0) {
            return new BigDecimal(value);
        }
        return new BigDecimal(value).divide(new BigDecimal(String.valueOf(Math.pow(10, decimal))),
                decimal, RoundingMode.HALF_UP);
    }

    /**
     * Number类型 转 String (保留数值位)
     *
     * @param number number
     * @param index  对应的format方法
     * @return
     */
    public static String DecimaltoString(Number number, Integer index) {
        switch (index) {
            case 1:
                return df1.get().format(number);
            default:
                return df1.get().format(number);
        }
    }

}
