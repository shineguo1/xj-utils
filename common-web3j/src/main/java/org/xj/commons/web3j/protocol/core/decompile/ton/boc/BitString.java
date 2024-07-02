package org.xj.commons.web3j.protocol.core.decompile.ton.boc;

import lombok.Getter;
import org.bouncycastle.util.Arrays;
import org.web3j.utils.Numeric;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/12 16:53
 */
public class BitString {
    public final static BitString EMPTY = new BitString("", 0, 0);

    /**
     * source bits
     */
    @Getter
    private final byte[] buffer;
    /**
     * The start index of bits.
     * Total bits length is `buffer.length * 8`, sometimes we cut a part of buffer as a new BitString.
     * We don't move any bit in 'buffer', but use 'offset' to sign the logical start index.
     * e.g. offset = 5, length =10, buffer = [0x0f, 0xd9, 0xd6] which means 0b 0000 1[111 1101 100]1 1101 0110
     * the bits between '[' and ']' is the effective BitString
     */
    private final int offset;
    private final int length;


    public BitString(String hexData, int offset, int length) {
        this(Numeric.hexStringToByteArray(hexData), offset, length);
    }

    public BitString(byte[] buffer, int offset, int length) {
        if (length < 0 || length + offset > buffer.length * 8) {
            throw new IndexOutOfBoundsException("Length " + length + " is out of bounds");
        }
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;
    }

    public int at(int index) {
        if (index >= this.length) {
            throw new IndexOutOfBoundsException(String.format("index %d > %d is out of bounds", index, this.length));
        }
        if (index < 0) {
            throw new IndexOutOfBoundsException(String.format("index %d < 0 is out of bounds", index));
        }
        int byteIndex = (this.offset + index) >> 3;
        int bitIndex = (this.offset + index) & 0b111 ^ 0b111; // bitIndex = 7 - i % 8
        return this.buffer[byteIndex] >> bitIndex & 0b1;
    }

    public BitString substring(int offset, int length) {

        // Check offset
        if (offset >= this.length) {
            throw new IndexOutOfBoundsException(String.format("Offset(%d) > %d is out of bounds", offset, this.length));
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException(String.format("Offset(%d) < 0 is out of bounds", offset));
        }

        // Corner case of empty string
        if (length == 0) {
            return BitString.EMPTY;
        }

        if (offset + length > this.length) {
            throw new IndexOutOfBoundsException(String.format("Offset(%d) + Length(%d) > %d is out of bounds", offset, length, this.length));
        }

        // Create substring
        return new BitString(this.buffer, this.offset + offset, length);
    }

    /**
     * Try to get a buffer from the bitstring without allocations
     * ${offset+this.offset} and ${length} must be 8*n, or return null;
     *
     * @param offset offset in bits
     * @param length length in bits
     * @returns buffer if the bitstring is aligned to bytes, null otherwise
     */
    public byte[] trySubBuffer(int offset, int length) {

        // Check offset
        if (offset >= this.length) {
            throw new IndexOutOfBoundsException("Offset is out of bounds");
        }
        if (offset < 0) {
            throw new IndexOutOfBoundsException("Offset is out of bounds");
        }
        if (offset + length > this.length) {
            throw new IndexOutOfBoundsException("Offset + Length is out of bounds");
        }

        // Check alignment
        if (length % 8 != 0) {
            return null;
        }
        if ((this.offset + offset) % 8 != 0) {
            return null;
        }

        // Create substring
        int start = ((this.offset + offset) >> 3);
        int end = start + (length >> 3);
        return Arrays.copyOfRange(this.buffer, start, end);
    }


    public int length() {
        return length;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BitString)) {
            //including obj == null
            return false;
        }
        BitString bitString = (BitString) obj;
        if (this.length != bitString.length) {
            return false;
        }
        for (int i = 0; i < this.length; i++) {
            if (this.at(i) != bitString.at(i)) {
                return false;
            }
        }
        return true;
    }

}
