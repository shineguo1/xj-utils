package org.xj.commons.web3j.protocol.core.decompile.ton.util;

import java.nio.ByteBuffer;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/11 16:22
 */
public class Crc32 {
    private final static int POLY = 0x82f63b78;

    /**
     * beta版：未找到测试数据来验证算法正确性
     */
    protected static byte[] crc32c(byte[] source) {
        int crc = 0 ^ 0xffffffff;
        for (int n = 0; n < source.length; n++) {
            crc ^= source[n];
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
            crc = (crc & 1) == 1 ? (crc >>> 1) ^ POLY : crc >>> 1;
        }
        crc = crc ^ 0xffffffff;

        // Convert endianness
        ByteBuffer res = ByteBuffer.allocate(4);
        res.putInt(crc);
        return res.array();
    }

}
