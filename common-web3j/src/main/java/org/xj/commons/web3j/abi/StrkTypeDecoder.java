package org.xj.commons.web3j.abi;

import org.xj.commons.toolkit.BigIntegerUtils;
import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.abi.datatypes.Address252;
import org.xj.commons.web3j.abi.datatypes.Uint252;
import com.google.common.collect.Lists;
import org.web3j.abi.TypeReference;
import org.web3j.abi.Utils;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/12 16:14
 */
public class StrkTypeDecoder {

    //decode
    //=====================================

    /**
     * @param input          输入数据集（cairo语法的服务返回的数组)
     * @param typeReferences 类型集合
     * @return
     */
    @SuppressWarnings("unchecked")
    public static List<Type> decode(List<String> input, List<TypeReference<Type>> typeReferences) {
        return decode(input, new Idx(0), typeReferences);
    }

    /**
     * @param input         输入数据（cairo语法的服务返回的数组)
     * @param typeReference 类型
     * @return
     */
    public static <T extends Type> T decode(List<String> input, TypeReference<T> typeReference) {
        return decode(input, new Idx(0), typeReference);
    }

    /**
     * @param input          输入数据集（cairo语法的服务返回的数组)
     * @param index          计数器。表示从input第x位开始解析，并且每读取一位，计数器会累加1。结束时会指向最新的未读取坐标。
     * @param typeReferences 类型集合
     * @return
     */
    public static List<Type> decode(List<String> input, Idx index, List<TypeReference<Type>> typeReferences) {
        int listSize = CollectionUtils.sizeOf(typeReferences);
        List<Type> result = Lists.newArrayList();
        for (int i = 0; i < listSize; i++) {
            Type obj = decode(input, index, typeReferences.get(i));
            result.add(obj);
        }
        return result;
    }

    /**
     * @param input         输入数据（cairo语法的服务返回的数组)
     * @param index         计数器。表示从input第x位开始解析，并且每读取一位，计数器会累加1。结束时会指向最新的未读取坐标。
     * @param typeReference 类型
     * @return
     */
    private static <T extends Type> T decode(List<String> input, Idx index, TypeReference<T> typeReference) {
        try {
            Class<T> classType = typeReference.getClassType();
            if (StaticStruct.class.isAssignableFrom(classType)) {
                //静态结构体
                return (T) decodeStaticStruct(input, index, (TypeReference) typeReference);
            } else if (DynamicArray.class.isAssignableFrom(classType)) {
                //动态数组
                return (T) decodeDynamicArray(input, index, (TypeReference) typeReference);
            }
            //虽然uint252在java中实际也占256位，但逻辑上是felt252位，cairo语法的返回值中占一位数据长度。在这里排除。
            else if (isUint256(classType)) {
                //cairo一位数据最多占252bit，所以uint256需要花费2位input
                T obj = (T) decodeUint256(input.get(index.value()), input.get(index.value() + 1), (Class) classType);
                index.add(2);
                return obj;
            } else {
                // 其余例如numeric、address、bool、string等占一长度的类型。
                // 动态结构体和静态数组暂时没有处理
                T obj = decode(input.get(index.value()), classType);
                index.add(1);
                return obj;
            }
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException("Unable to access parameterized type "
                    + Utils.getTypeName(typeReference.getType()), e);
        }
    }

    public static <T extends Type> boolean isUint256(Class<T> classType) {
        boolean isUintHold256Bit = Uint.class.equals(classType) || Uint256.class.isAssignableFrom(classType);
        //uint252在java中占256位，但逻辑上(cairo语法中)是felt252位，cairo语法的返回值中占一位数据长度。在这里排除。
        return !Uint252.class.isAssignableFrom(classType) && isUintHold256Bit;
    }

    /**
     * Uint256占返回数组的2位，每一位最高32个hex字符，累计64个hex字符
     *
     * @param lowerBitHex  数的低位（最多32位）
     * @param higherBitHex 数的高位（最多32位）
     * @return
     */
    private static <T extends NumericType> T decodeUint256(String lowerBitHex, String higherBitHex, Class<T> type) {
        BigInteger lowerBit = BigIntegerUtils.decode(lowerBitHex);
        BigInteger higherBit = BigIntegerUtils.decode(higherBitHex);
        //1位16进制等于4位二进制，所以高位左移32*4位，再加上低位。
        BigInteger value = higherBit.shiftLeft(32 * 4).add(lowerBit);
        return instantiateNumeric(type, value);
    }

    /**
     * 解析占cairo数据一位的数据类型。cairo基础数据类型最多占252bit。
     *
     * @param input 输入数据
     * @param type  类型
     * @param <T>   解析类型的泛型
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends Type> T decode(String input, Class<T> type) {
        if (NumericType.class.isAssignableFrom(type)) {
            return (T) decodeNumeric(input, (Class<NumericType>) type);
        } else if (Address252.class.isAssignableFrom(type)) {
            return (T) decodeAddress252(input);
        } else if (Address.class.isAssignableFrom(type)) {
            return (T) decodeAddress(input);
        } else if (Bool.class.isAssignableFrom(type)) {
            return (T) decodeBool(input);
        } else if (Utf8String.class.isAssignableFrom(type)) {
            return (T) decodeUtf8String(input);
        } else if (Array.class.isAssignableFrom(type)) {
            throw new UnsupportedOperationException(
                    "Array types must be wrapped in a TypeReference");
        } else {
            throw new UnsupportedOperationException("Type cannot be encoded: " + type.getClass());
        }
    }

    private static Utf8String decodeUtf8String(String input) {
        String str = new String(Numeric.hexStringToByteArray(input), StandardCharsets.UTF_8);
        return new Utf8String(str);
    }

    private static <T extends NumericType> T decodeNumeric(String input, Class<T> type) {
        return instantiateNumeric(type, BigIntegerUtils.decode(input));
    }

    private static Address252 decodeAddress252(String input) {
        return new Address252(input);
    }

    private static Bool decodeBool(String input) {
        BigInteger numericValue = Numeric.toBigInt(input);
        boolean value = numericValue.equals(BigInteger.ONE);
        return new Bool(value);
    }

    public static Address decodeAddress(String input) {
        BigInteger number = BigIntegerUtils.decode((input));
        //len必须是8的倍数
        int len = ((number.bitLength() - 1) / 8 + 1) * 8;
        return new Address(len, input);
    }


    private static <T extends Type> DynamicArray<T> decodeDynamicArray(List<String> input, Idx idx, TypeReference<DynamicArray<T>> typeReference) {
        try {
            Class<T> cls = getParameterizedTypeFromArray(typeReference);
            TypeReference<T> elementTypeReference = TypeReference.create(cls);

            //第一位是动态数组长度
            Uint uArrayLen = decode(input.get(idx.value()), Uint.class);
            idx.add(1);

            long arrayLen = uArrayLen.getValue().longValue();
            List<TypeReference<Type>> paramTypeReferences = Lists.newArrayList();
//            List<T> params = Lists.newArrayList();
            for (int i = 1; i <= arrayLen; i++) {
                paramTypeReferences.add((TypeReference<Type>) elementTypeReference);
            }
            List<Type> params = decode(input, idx, paramTypeReferences);
            return new DynamicArray<>(cls, (List<T>) params);
        } catch (
                ClassNotFoundException e) {
            throw new UnsupportedOperationException("Unable to access parameterized type "
                    + Utils.getTypeName(typeReference.getType()), e);
        }
    }

    private static <T extends StaticStruct> T decodeStaticStruct(List<String> input, Idx idx, TypeReference<T> typeReference) {
        try {
            Class<T> classType = typeReference.getClassType();
            Constructor<?> constructor = Utils.findStructConstructor(classType);
            Class<Type>[] parameterTypes = (Class<Type>[]) constructor.getParameterTypes();
            //静态Struct中所有子成员的类型
            List<TypeReference<Type>> paramsTypeReferences = Arrays.stream(parameterTypes).map(TypeReference::create).collect(Collectors.toList());
            //递归
            List<Type> elements = decode(input, idx, paramsTypeReferences);
            return instantiateStruct(typeReference, elements);
        } catch (
                ClassNotFoundException e) {
            throw new UnsupportedOperationException("Unable to access parameterized type "
                    + Utils.getTypeName(typeReference.getType()), e);
        }
    }

    // instantiate class
    //==================================================

    @SuppressWarnings("unchecked")
    private static <T extends Type> T instantiateStruct(
            final TypeReference<T> typeReference, final List<Type> parameters) {
        try {
            Class<T> classType = typeReference.getClassType();
            Constructor ctor = Utils.findStructConstructor(classType);
            ctor.setAccessible(true);
            return (T) ctor.newInstance(parameters.toArray());
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(
                    "Constructor cannot accept" + Arrays.toString(parameters.toArray()), e);
        }
    }


    @SuppressWarnings("unchecked")
    private static <T extends NumericType> T instantiateNumeric(
            final Class<T> classType, final BigInteger num) {
        try {
            Constructor<T> ctor = classType.getConstructor(BigInteger.class);
            ctor.setAccessible(true);
            return ctor.newInstance(num);
        } catch (ReflectiveOperationException e) {
            throw new UnsupportedOperationException(
                    "Constructor cannot accept" + num, e);
        }
    }

    //copy internal method from TypeDecoder
    //===================================================

    static <T extends Type> Class<T> getParameterizedTypeFromArray(TypeReference typeReference)
            throws ClassNotFoundException {

        java.lang.reflect.Type type = typeReference.getType();
        java.lang.reflect.Type[] typeArguments =
                ((ParameterizedType) type).getActualTypeArguments();

        String parameterizedTypeName = Utils.getTypeName(typeArguments[0]);
        return (Class<T>) Class.forName(parameterizedTypeName);
    }

    // internal struct
    // ===================================

    public static class Idx {

        Idx(int num) {
            value = num;
        }

        private int value = 0;

        public int value() {
            return value;
        }

        public void add(int step) {
            value += step;
        }

    }

}
