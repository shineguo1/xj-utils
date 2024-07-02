package org.xj.commons.web3j.protocol.core.decompile.ton.boc;

import org.xj.commons.toolkit.MathUtil;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 11:09
 */
public class BitBuilder {

    private byte[] _buffer;
    private int _length;

    public BitBuilder(int size) {
        //1 byte=8 bits, 向上取整
        int allocLen = MathUtil.ceilDiv(size, 8);
        this._buffer = new byte[allocLen];
        this._length = 0;
    }

    public byte[] buffer() {
        return _buffer;
    }

    /**
     * Copy bits from BitString
     *
     * @param src source bits
     */
    public void writeBits(BitString src) {
        for (int i = 0; i < src.length(); i++) {
            this.writeBit(src.at(i));
        }
    }

    /**
     * Write a single bit
     *
     * @param value bit to write, true or positive number for 1, false or zero or negative for 0
     */
    public void writeBit(int value) {

        // Check overflow
        int n = this._length;
        if (n > this._buffer.length * 8) {
            throw new IndexOutOfBoundsException("BitBuilder overflow");
        }

        // Set bit
        if (value > 0) {
            this._buffer[n / 8] |= 1 << (7 - (n % 8));
        }

        // Advance
        this._length++;
    }

}
