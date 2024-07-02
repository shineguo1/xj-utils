package org.xj.commons.web3j.protocol.core.decompile.ton.util;


import com.google.common.hash.HashFunction;
import org.web3j.utils.Numeric;

import java.nio.ByteBuffer;

class Crc32Test {


    public static void main(String[] args) {
        HashFunction hashFunction = com.google.common.hash.Hashing.crc32c();
        ByteBuffer res = ByteBuffer.allocate(4);
        res.putInt(123456789);
        byte[] intput = res.array();
        byte[] b1 = hashFunction.hashBytes(intput).asBytes();
        byte[] b2 = Crc32.crc32c(intput);
        String s = Numeric.toHexString(b1);
        String s2 = Numeric.toHexString(b2);
        System.out.println(s);
        System.out.println(s2);
    }
}