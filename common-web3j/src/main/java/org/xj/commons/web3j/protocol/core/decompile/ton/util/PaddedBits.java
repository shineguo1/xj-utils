package org.xj.commons.web3j.protocol.core.decompile.ton.util;

import org.xj.commons.toolkit.MathUtil;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.BitBuilder;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.BitString;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 10:42
 */
public class PaddedBits {
    public static byte[] bitsToBuffer(BitString bits) {

        // Create builder
        int len = MathUtil.ceilDiv(bits.length(), 8) * 8;
        BitBuilder builder = new BitBuilder(len);
        builder.writeBits(bits);
        return builder.buffer();
    }
    public static byte[] bitsToPaddedBuffer(BitString bits) {

        // Create builder
        int len = MathUtil.ceilDiv(bits.length(), 8) * 8;
        BitBuilder builder = new BitBuilder(len);
        builder.writeBits(bits);

        // Apply padding
        int padding =  len - bits.length();
        for (int i = 0; i < padding; i++) {
            if (i == 0) {
                builder.writeBit(1);
            } else {
                builder.writeBit(0);
            }
        }

        return builder.buffer();
    }
}
