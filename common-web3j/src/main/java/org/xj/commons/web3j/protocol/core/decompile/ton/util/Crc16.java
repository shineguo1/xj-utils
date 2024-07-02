package org.xj.commons.web3j.protocol.core.decompile.ton.util;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/11 16:22
 */
public class Crc16 {

    public static byte[] crc16(byte[] data) {
        int poly = 0x1021;
        int reg = 0;
        for (byte b : data) {
            int mask = 0x80;
            while (mask > 0) {
                reg <<= 1;
                if ((b & mask) != 0) {
                    reg += 1;
                }
                mask >>= 1;
                if (reg > 0xffff) {
                    reg &= 0xffff;
                    reg ^= poly;
                }
            }
        }
        return new byte[]{(byte) Math.floorDiv(reg, 256), (byte) (reg % 256)};
    }
}
