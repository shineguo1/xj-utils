package org.xj.commons.web3j.abi;

import com.google.common.collect.Lists;
import org.web3j.abi.TypeDecoder;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Int24;
import org.web3j.abi.datatypes.generated.Uint128;
import org.web3j.abi.datatypes.generated.Uint16;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint32;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;
import org.xj.commons.toolkit.DecimalUtils;

import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author : xj
 * @date : 2023/1/9 14:57
 */
public class FunctionUtils {
    public static final BigDecimal E18 = BigDecimal.valueOf(1e18);
    public static final BigInteger E18_INT = DecimalUtils.objToBigInteger(E18);

    public static Function emptyToUint256(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function emptyToUint256s(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
    }

    public static Function uint256ToUint256(String name, BigInteger bigInteger) {
        return new Function(name,
                Collections.singletonList(new Uint256(bigInteger)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function boolToUint256(String name, Boolean boo) {
        return new Function(name,
                Collections.singletonList(new Bool(boo)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint256ToUint256(String name, Integer integer) {
        return new Function(name,
                Collections.singletonList(new Uint256(integer)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint128ToUint256(String name, BigInteger bigInteger) {
        return new Function(name,
                Collections.singletonList(new Uint128(bigInteger)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint256ToAddress(String name, BigInteger bigInteger) {
        return new Function(name,
                Collections.singletonList(new Uint256(bigInteger)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function uint256ToAddress(String name, Integer i) {
        return new Function(name,
                Collections.singletonList(new Uint256(i)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function uint8ToAddress(String name, Integer i) {
        return new Function(name,
                Collections.singletonList(new Uint8(i)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function emptyToAddress(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function emptyToBool(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Bool.class)));
    }

    public static Function emptyToAddresses(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(new TypeReference<DynamicArray<Address>>() {
                }));
    }

    public static Function addressToUint256s(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(new TypeReference<DynamicArray<Uint256>>() {
                }));
    }

    public static Function addressToAddresses(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(new TypeReference<DynamicArray<Address>>() {
                }));
    }

    public static Function emptyToString(String name) {
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(TypeReference.create(Utf8String.class)));
    }

    public static Function toUint256(String name, Object... input) {
        return toBo(name, Uint256.class, input);
    }

    public static Function toBool(String name, Object... input) {
        return toBo(name, Bool.class, input);
    }

    public static <T extends Type> Function toBo(String name, Class<T> clazz, Object... input) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        List<Type> inputParameters = parameters(input);
        return new Function(name,
                inputParameters,
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function toBoList(String name, Class<T> clazz, Object... input) {
        TypeReference<DynamicArray<T>> dynamicArray = getDynamicArray(clazz, true);
        List<Type> inputParameters = parameters(input);
        return new Function(name,
                inputParameters,
                Collections.singletonList(dynamicArray));
    }

    private static List<Type> parameters(Object... input) {
        List<Type> list = new ArrayList<>();
        for (Object o : input) {
            if (o instanceof String) {
                String o1 = (String) o;
                list.add(new Address(o1));
            } else if (o instanceof BigInteger) {
                BigInteger o1 = (BigInteger) o;
                list.add(new Uint256(o1));
            } else if (o instanceof Integer) {
                Integer o1 = (Integer) o;
                list.add(new Uint256(o1));
            } else if (o instanceof Boolean) {
                Boolean o1 = (Boolean) o;
                list.add(new Bool(o1));
            } else if (o instanceof Type) {
                Type o1 = (Type) o;
                list.add(o1);
            }
        }
        return list;
    }

    public static Function addressToUint256List(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class)));
    }

    public static Function addressToUint256List3(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Arrays.asList(TypeReference.create(Uint256.class), TypeReference.create(Uint256.class), TypeReference.create(Uint256.class)));
    }

    public static String toByte32Str(String k) {
        byte[] byteValue = k.getBytes();
        byte[] byteValueLen32 = new byte[32];
        System.arraycopy(byteValue, 0, byteValueLen32, 0, byteValue.length);
        return Numeric.toHexString(byteValueLen32);
    }

    public static String keccak256(Object... keys) {
        List<Type> list = Lists.newArrayList();
        for (Object key : keys) {
            if (key instanceof String) {
                list.add(new Address(key.toString()));
            }
            if (key instanceof Boolean) {
                list.add(new Bool((Boolean) key));
            }
            if (key instanceof Integer) {
                list.add(new Uint8((Integer) key));
            }
        }
        //等价于abi.encode(string)
        String abi = TypeEncoder.encode(new DynamicStruct(list));
        //等价于keccak256(abi.encode(string))
        String hash = Hash.sha3(abi);
        return hash;
    }


    public static String keccak256(Type... keys) {
        //等价于abi.encode(string)
        String abi = TypeEncoder.encode(new DynamicStruct(keys));
        //等价于keccak256(abi.encode(string))
        String hash = Hash.sha3(abi);
        return hash;
    }


    private static String encodeParameters(List<Type> parameters) {
        StringBuilder result = new StringBuilder();
        for (Type parameter : parameters) {
            String encodedValue = TypeEncoder.encode(parameter);
            encodedValue = encodedValue.replaceAll("^(0+)", "");
            if (encodedValue.length() % 2 != 0) {
                encodedValue = "0" + encodedValue;
            }
            result.append(encodedValue);
        }
        return result.toString();
    }

    public static String positionKey(String owner, BigInteger tickLower, BigInteger tickUpper) {
        String params = encodeParameters(Arrays.asList(new Address(owner), new Int24(tickLower),
                new Int24(tickUpper)));
        return Hash.sha3(params);
    }

    public static Function byte32ToAddress(String name, String byte32) {
        byte[] byte320 = Numeric.hexStringToByteArray(byte32);
        return new Function(name,
                Collections.singletonList(new Bytes32(byte320)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function byte32ToUint256(String name, String byte32) {
        byte[] byte320 = Numeric.hexStringToByteArray(byte32);
        return new Function(name,
                Collections.singletonList(new Bytes32(byte320)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }


    public static Function doubleByte32ToAddress(String name, String byte32a, String byte32b) {
        byte[] byte32a0 = Numeric.hexStringToByteArray(byte32a);
        byte[] byte32b0 = Numeric.hexStringToByteArray(byte32b);
        return new Function(name,
                Arrays.asList(new Bytes32(byte32a0), new Bytes32(byte32b0)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function addressUint256ToUint256(String name, String address, Long i) {
        return new Function(name,
                Arrays.asList(new Address(address), new Uint256(i)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function addressUint256ToUint256(String name, String address, BigInteger i) {
        return new Function(name,
                Arrays.asList(new Address(address), new Uint256(i)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint256AddressToUint256(String name, Long i, String address) {
        return new Function(name,
                Arrays.asList(new Uint256(i), new Address(address)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint256AddressToUint256(String name, BigInteger i, String address) {
        return new Function(name,
                Arrays.asList(new Uint256(i), new Address(address)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint16AddressToUint256(String name, Integer i, String address) {
        return new Function(name,
                Arrays.asList(new Uint16(i), new Address(address)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function uint8AddressToUint256(String name, Integer i, String address) {
        return new Function(name,
                Arrays.asList(new Uint8(i), new Address(address)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function addressToUint256(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function addressToBool(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(TypeReference.create(Bool.class)));
    }

    public static Function addressToAddress(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(TypeReference.create(Address.class)));
    }

    public static Function addressToBytes32(String name, String address) {
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(TypeReference.create(Bytes32.class)));
    }

    public static Function doubleAddressToUint256(String name, String address1, String address2) {
        return new Function(name,
                Arrays.asList(new Address(address1), new Address(address2)),
                Collections.singletonList(TypeReference.create(Uint256.class)));
    }

    public static Function doubleAddressToBool(String name, String address1, String address2) {
        return new Function(name,
                Arrays.asList(new Address(address1), new Address(address2)),
                Collections.singletonList(TypeReference.create(Bool.class)));
    }

    public static <T extends Type> Function addressToBoList(String name, String address, Class<T> clazz) {
        TypeReference<DynamicArray<T>> dynamicArray = getDynamicArray(clazz, true);
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(dynamicArray));
    }


    public static <T extends Type> TypeReference<DynamicArray<T>> getDynamicArray(Class<T> clazz, boolean indexed) {
        TypeReference<T> arrayWrappedType = TypeReference.create(clazz, indexed);
        return new TypeReference<DynamicArray<T>>(indexed) {
            TypeReference getSubTypeReference() {
                return (TypeReference) arrayWrappedType;
            }

            public java.lang.reflect.Type getType() {
                return new ParameterizedType() {
                    public java.lang.reflect.Type[] getActualTypeArguments() {
                        return new java.lang.reflect.Type[]{((TypeReference) arrayWrappedType).getType()};
                    }

                    public java.lang.reflect.Type getRawType() {
                        return DynamicArray.class;
                    }

                    public java.lang.reflect.Type getOwnerType() {
                        return Class.class;
                    }
                };
            }
        };
    }

    public static <T extends Type> Function addressToBo(String name, String address, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        return new Function(name,
                Collections.singletonList(new Address(address)),
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function byte32ToBo(String name, String byte32, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        byte[] byte320 = Numeric.hexStringToByteArray(byte32);
        return new Function(name,
                Collections.singletonList(new Bytes32(byte320)),
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function byte32ToBo(String name, Bytes32 byte32, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        return new Function(name,
                Collections.singletonList(byte32),
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function uint256ToBo(String name, Integer idx, Class<T> clazz) {
        return uint256ToBo(name, BigInteger.valueOf(idx), clazz);
    }

    public static <T extends Type> Function uint256ToBo(String name, BigInteger idx, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        return new Function(name,
                Collections.singletonList(new Uint256(idx)),
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function uint32ToBo(String name, BigInteger idx, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        return new Function(name,
                Collections.singletonList(new Uint32(idx)),
                Collections.singletonList(typeReference));
    }

    public static <T extends Type> Function emptyToBo(String name, Class<T> clazz) {
        TypeReference<T> typeReference = TypeReference.create(clazz, true);
        return new Function(name,
                Collections.emptyList(),
                Collections.singletonList(typeReference));
    }

    public static String decodeHexAsString(String input) {
        return new String(Numeric.hexStringToByteArray(input), StandardCharsets.UTF_8);
    }

    public static Address decodeHexAsAddress(String input) {
        String s = input.length() >= 2 ? input.substring(2) : "";
        return TypeDecoder.decode(String.format("%64s", s).replace(' ', '0')
                , Address.class);
    }


}
