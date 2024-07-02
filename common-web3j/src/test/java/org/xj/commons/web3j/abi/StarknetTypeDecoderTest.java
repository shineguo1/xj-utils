package org.xj.commons.web3j.abi;


import org.xj.commons.web3j.abi.datatypes.Address252;
import lombok.Data;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint104;
import org.web3j.abi.datatypes.generated.Uint8;

import java.util.Arrays;
import java.util.List;

public class StarknetTypeDecoderTest {

    public static void main(String[] args) {
        test_dynamic_array();
    }

    private static void test_dynamic_array() {
        List<String> params = params();
        DynamicArray<Address252> decode = StrkTypeDecoder.decode(params,
                new TypeReference<DynamicArray<Address252>>() {
                });
        System.out.println(decode.getValue());
    }

    private static void test_static_struct() {
        List<String> params = params();
        TestStaticStruct decode = StrkTypeDecoder.decode(params, TypeReference.create(TestStaticStruct.class));
        System.out.println(decode);
    }

    private static List<String> params() {
        return Arrays.asList("0x9",
                "0x5ef8800d242c5d5e218605d6a10e81449529d4144185f95bf4b8fb669424516",
                "0x285aa1c4bbeef8a183fb7245f096ddc4c99c6b2fedd1c1af52a634c83842804",
                "0x52b136b37a7e6ea52ce1647fb5edc64efe23d449fc1561d9994a9f8feaa6753",
                "0x5ae9c593b2bef20a8d69ae7abf1e6da551481f9efd83d03a9f05b6d7c9a78ec",
                "0xc318445d5a5096e2ad086452d5c97f65a9d28cafe343345e0fa70da0841295",
                "0x68400056dccee818caa7e8a2c305f9a60d255145bac22d6c5c9bf9e2e046b71",
                "0x7ae43abf704f4981094a4f3457d1abe6b176844f6cdfbb39c0544a635ef56b0",
                "0x33c4141c8eb6ab8e7506c6f09c1a64b0995c9a5fa2ba6fa827845535b942786",
                "0x1d9f197585bf03b368b6967538b41e6e45cc1656f4265cd253c3aa2aaf92509");
    }

    static void test_decode_uint() {
        Uint104 decode = StrkTypeDecoder.decode("0xf", Uint104.class);
        System.out.println(decode);
    }

    static void test_decode_string() {
        Utf8String decode = StrkTypeDecoder.decode("0x4d6567612050616e69632e", Utf8String.class);
        System.out.println(decode);
    }

    @Data
    public static class TestStaticStruct extends StaticStruct {
        public TestStaticStruct(
                Uint8 num,
                Address252 address0,
                Address252 address1,
                Address252 address2,
                Address252 address3,
                Address252 address4,
                Address252 address5
        ) {
            super(num, address0, address1, address2, address3, address4, address5);
        }
    }


//    public static void main(String[] args) {
//        Address252 decode = TypeDecoder.decode("0x5ef8800d242c5d5e218605d6a10e81449529d4144185f95bf4b8fb669424516",
//                Address252.class);
//
//        Address decode2 = TypeDecoder.decode("0x5ef8800d242c5d5e218605d6a10e81449529d4144185f95bf4b8fb669424516",
//                Address.class);
//        Address address1 = new Address(256, "0x5ef8800d242c5d5e218605d6a10e81449529d4144185f95bf4b8fb669424516");
//        System.out.println(keccak256("CrocSwap(address,address,uint256,bool,bool,uint128,uint16,uint128,uint128,uint8,int128,int128)").toString(16));
//        System.out.println(keccak256("CrocMicroSwap(bytes,bytes)").toString(16));
//        System.out.println(keccak256("Transfer").and(MASK_250).toString(16));
//        System.out.println(keccak256("TransactionExecuted").and(MASK_250).toString(16));
//    }
}