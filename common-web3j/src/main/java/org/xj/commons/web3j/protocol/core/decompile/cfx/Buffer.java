package org.xj.commons.web3j.protocol.core.decompile.cfx;

import java.nio.ByteBuffer;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/8/25 10:37
 */
public class Buffer {
    public static byte[] from(Object value) {
        if (value instanceof String) {
            return ((String) value).getBytes();
        } else if (value instanceof byte[]) {
            return (byte[]) value;
        } else if (value instanceof Byte[]) {
            Byte[] arr = (Byte[]) value;
            byte[] bytes = new byte[arr.length];
            for (int i = 0; i < arr.length; i++) {
                bytes[i] = arr[i];
            }
            return bytes;
        } else if (value instanceof ByteBuffer) {
            ByteBuffer buffer = (ByteBuffer) value;
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            return bytes;
        } else {
            throw new IllegalArgumentException("Unsupported input type");
        }
    }

}
