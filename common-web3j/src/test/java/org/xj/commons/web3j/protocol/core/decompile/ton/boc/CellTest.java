package org.xj.commons.web3j.protocol.core.decompile.ton.boc;


import org.xj.commons.toolkit.BigIntegerUtils;

import java.math.BigInteger;
import java.util.Base64;

class CellTest {

    //TEST
    //====================================================

    public static void main(String[] args) {
        //      [
        //       "cell",
        //        {
        //          "bytes": "te6cckEBAQEAJAAAQ4AO87mQKicbKgHIk4pSPP4k5xhHqutqYgAB7USnesDnCdASkmt4",
        //          "object": {
        //            "data": {
        //              "b64": "gA7zuZAqJxsqAciTilI8/iTnGEeq62piAAHtRKd6wOcJwA==",
        //              "len": 267
        //            },
        //            "refs": [],
        //            "special": false
        //          }
        //        }
        //      ],
//        byte[] decodeBytes = Base64.getDecoder().decode("gA7zuZAqJxsqAciTilI8/iTnGEeq62piAAHtRKd6wOcJwA==");
        byte[] decodeBytes = Base64.getDecoder().decode("AWh0dHBzOi8vbHAuc3Rvbi5maS8wOjIxMEY5OThENjE1RkYzMzdDOTU0MUNFMTE1NkVFODUyNUY1RjU0QUI3RjQwMjQxNjYyNkQ4M0U5NjY3MTgzQTMuanNvbg");

//        TonAddress account = new BitReader(new BitString(decodeBytes, 0, decodeBytes.length * 8)).loadMaybeAddress();
        byte[] buffer = new BitReader(new BitString(decodeBytes, 0, decodeBytes.length * 8)).loadBuffer(728 / 8);
        System.out.println(new String(decodeBytes));
//        byte[] bytes = Base64.getDecoder().decode("te6cckECCQEAASEAAQMAwAECASACAwIBbgQFAUO/90B+l48BpAcRQRsay3c6lr3ZP6g7tcqENQE8jEs6yR9ACAFBvwQXWzHavQAMl6U1YjOURvlv9u3O0N9xR7KQ5F9ekPj2BgFBvwOXXtqKbOS+FB2/hXKLyBTxWwjOHKMTjPJ6N2x7oJ8yBwCcAGh0dHBzOi8vYnJpZGdlLnRvbi5vcmcvdG9rZW4vMS8weGRhYzE3Zjk1OGQyZWU1MjNhMjIwNjIwNjk5NDU5N2MxM2Q4MzFlYzcucG5nAJ4AaHR0cHM6Ly9icmlkZ2UudG9uLm9yZy90b2tlbi8xLzB4ZGFjMTdmOTU4ZDJlZTUyM2EyMjA2MjA2OTk0NTk3YzEzZDgzMWVjNy5qc29uAAQANmruVQ8=");
//        Numeric.toHexString(bytes);
    }

    public static void test1() {
    /*
    02 01 c0 02 01
    01 01 ff 02
    00 06 0aaaaa

    固定前缀
    b5ee9c72 xx xx xx xx 00 xx 00
         */
        System.out.println("80022a16a3164c4d5aa3133f3110ff10496e00ca8ac8abeffc5027e024d33480c3f0".length());
        //b5ee9c7201010101002400004380022a16a3164c4d5aa3133f3110ff10496e00ca8ac8abeffc5027e024d33480c3f0

        /*
         serialization.writeBytes(reachBocMagicPrefix);  // 0xb5ee9c72
        serialization.writeBitArray([has_idx, hash_crc32, has_cache_bits]); //0b000 (0b表示2进制)
        serialization.writeUint(flags, 2); // 0b00
        serialization.writeUint(s_bytes, 3); // 0b001
        serialization.writeUint8(offset_bytes); // 0x01
        serialization.writeUint(cells_num, s_bytes * 8); // 0x01
        serialization.writeUint(1, s_bytes * 8); // One root for now  0x01
        serialization.writeUint(0, s_bytes * 8); // Complete BOCs only 0x00
        serialization.writeUint(full_size, offset_bytes * 8); // 0x5d
        serialization.writeUint(0, s_bytes * 8); // Root shoulh have index 0 0x00
        */

        /*
          b5ee9c72 01 01 01 01 00 5d 00
          00 b6 0168747470733a2f2f6c702e73746f6e2e66692f303a323130463939384436313546463333374339353431434531313536454538353235463546353441423746343032343136363236443833453936363731383341332e6a736f6e

          b5ee9c72 01 01 01 01 00 24 00
          00 43 800ef3b9902a271b2a01c8938a523cfe24e71847aaeb6a620001ed44a77ac0e709d0
          0b100 00000000 hashpart

         */

        System.out.println("===");
        BigInteger num1 = BigIntegerUtils.decode("0x1150b518b2626ad51899f98887f8824b70065456455f7fe2813f012699a4061f");
        BigInteger num2 = BigIntegerUtils.decode("0x80022a16a3164c4d5aa3133f3110ff10496e00ca8ac8abeffc5027e024d33480c3f0");
        System.out.println(num1.toString(2));
        System.out.println(num2.toString(2));
        System.out.println("0001000101010000101101010001100010110010011000100110101011010101000110001001100111111001100010001000011111111000100000100100101101110000000001100101010001010110010001010101111101111111111000101000000100111111000000010010011010011001101001000000011000011111");
        System.out.println("10000000000000100010101000010110101000110001011001001100010011010101101010100011000100110011111100110001000100001111111100010000010010010110111000000000110010101000101011001000101010111110111111111100010100000010011111100000001001001101001100110100100000001100001111110000");
        System.out.println("===");

        calc(0);
        calc(1);
        calc(7);
        calc(8);
        calc(9);
        calc(24);
        calc(56);
//00 43 80000000000000000000000000000000000000000000000000000000000000000010
//00 43 80022a16a3164c4d5aa3133f3110ff10496e00ca8ac8abeffc5027e024d33480c3f0

    }

    private static void calc(int b) {
        //bits: 0-7->1 8->2 9--15>3, 16->4 15-23->5, 24->6
        // floor(b/8) + ceil(b/8) = b>>3 + (b>>3 + b&7 != 0)
        int quotient = b >> 3; // b/8

        int remainder = b & 7; // 使用位运算与运算符&（7即二进制表示为111）得到余数，这个余数用于确定向上取整的条件

        int result = quotient * 2 + ((remainder == 0) ? 0 : 1); // 最终结果为向下取整的商加上一个向上取整的条件（如果余数不为0，则向上取整结果加1）

        System.out.println("结果为：" + result);  // 输出结果
    }

}