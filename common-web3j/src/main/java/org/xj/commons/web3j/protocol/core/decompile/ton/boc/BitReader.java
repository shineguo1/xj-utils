package org.xj.commons.web3j.protocol.core.decompile.ton.boc;

import org.xj.commons.web3j.protocol.core.decompile.ton.address.TonAddress;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.Stack;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/13 14:26
 */
public class BitReader {

    private final BitString _bits;
    private int _offset;
    private final Stack<Integer> _checkpoints;

    public BitReader(BitString bits) {
        this(bits, 0);
    }

    public BitReader(BitString bits, int offset) {
        this._checkpoints = new Stack<>();
        this._bits = bits;
        this._offset = offset;
    }

    /**
     * Offset in source bit string
     */
    public int offset() {
        return this._offset;
    }

    /**
     * Number of bits remaining
     */
    public int remaining() {
        return this._bits.length() - this._offset;
    }

    /**
     * Skip bits
     *
     * @param bits number of bits to skip
     */
    public void skip(int bits) {
        if (bits < 0 || this._offset + bits > this._bits.length()) {
            throw new IndexOutOfBoundsException(String.format("Index %d is out of bounds", this._offset + bits));
        }
        this._offset += bits;
    }

    /**
     * Reset to the beginning or latest checkpoint
     */
    public void reset() {
        if (this._checkpoints.size() > 0) {
            this._offset = this._checkpoints.pop();
        } else {
            this._offset = 0;
        }
    }

    /**
     * Save checkpoint
     */
    public void save() {
        this._checkpoints.push(this._offset);
    }


    /**
     * Load a single bit
     *
     * @returns true if the bit is set, false otherwise
     */
    public int loadBit() {
        int r = this.preloadBit();
        this._offset++;
        return r;
    }

    /**
     * Preload bit
     *
     * @returns true if the bit is set, false otherwise
     */
    public int preloadBit() {
        return this._bits.at(this._offset);
    }

    /**
     * Load bit string
     *
     * @param bits number of bits to read
     * @returns new bitstring
     */
    public BitString loadBits(int bits) {
        BitString r = this.preloadBits(bits);
        this._offset += bits;
        return r;
    }

    /**
     * Preload bit string
     *
     * @param bits number of bits to read
     * @returns new bitstring
     */
    public BitString  preloadBits(int bits) {
        return this._bits.substring(this._offset, bits);
    }

    /**
     * Load buffer
     *
     * @param bytes number of bytes
     * @returns new buffer
     */
    public byte[] loadBuffer(int bytes) {
        byte[] buf = this._preloadBuffer(bytes, this._offset);
        this._offset += bytes * 8;
        return buf;
    }

    /**
     * Load uint value
     *
     * @param bits uint bits
     * @returns read value as number
     */
    public int loadUint(int bits) {
        int uint = this._preloadUint(bits, this._offset);
        this._offset += bits;
        return uint;
    }

    /**
     * Preload varuint value
     *
     * @param bits number of bits to read the size
     * @returns read value as bigint
     */
    public int preloadVarUint(int bits) {
        int size = this._preloadUint(bits, this._offset);
        return this._preloadUint(size * 8, this._offset + bits);
    }

    public TonAddress loadMaybeAddress() {
        int type = this._preloadUint(2, this._offset);
        if (type == 0) {
            this._offset += 2;
            return null;
        } else if (type == 2) {
            return this._loadInternalAddress();
        } else {
            throw new IllegalArgumentException("Unreachable");
        }
    }

    /**
     * Read address of any type
     *
     * @returns Address or ExternalAddress or null
     */
    public Object loadAddressAny() {
        int type = this._preloadUint(2, this._offset);
        if (type == 0) {
            this._offset += 2;
            return null;
        } else if (type == 2) {
            return this._loadInternalAddress();
        } else if (type == 1) {
            return this._loadExternalAddress();
        } else if (type == 3) {
            throw new IllegalArgumentException("Unsupported");
        } else {
            throw new IllegalArgumentException("Unreachable");
        }
    }


    /**
     * Load bit string that was padded to make it byte alligned. Used in BOC serialization
     *
     * @param bits number of bits to read
     */
    public BitString loadPaddedBits(int bits) {

        // Check that number of bits is byte alligned
        if (bits % 8 != 0) {
            throw new InvalidParameterException("Invalid number of bits");
        }

        // Skip padding
        int length = bits;
        while (true) {
            if (this._bits.at(this._offset + length - 1) == 1) {
                length--;
                break;
            } else {
                length--;
            }
        }

        // Read substring
        BitString r = this._bits.substring(this._offset, length);
        this._offset += bits;
        return r;
    }

    /**
     * Preload int from specific offset
     *
     * @param bits   bits to preload
     * @param offset offset to start from
     * @returns read value as int
     */
    private int _preloadInt(int bits, int offset) {
        if (bits == 0) {
            return 0;
        }
        int sign = this._bits.at(offset);
        int res = 0;
        for (int i = 0; i < bits - 1; i++) {
            if (this._bits.at(offset + 1 + i) == 1) {
                res += 1 << (bits - i - 1 - 1);
            }
        }
        if (sign == 1) {
            res = res - (1 << (bits - 1));
        }
        return res;
    }


    /**
     * Preload uint from specific offset
     *
     * @param bits   bits to preload
     * @param offset offset to start from
     * @returns read value as int
     */
    private int _preloadUint(int bits, int offset) {
        if (bits == 0) {
            return 0;
        }
        int res = 0;
        for (int i = 0; i < bits; i++) {
            if (this._bits.at(offset + i) == 1) {
                res += 1 << (bits - i - 1);
            }
        }
        return res;
    }

    /**
     * Preload uint from specific offset
     *
     * @param bits   bits to preload
     * @param offset offset to start from
     * @returns read value as bigint
     */
    private BigInteger _preloadUBigInt(int bits, int offset) {
        if (bits == 0) {
            return BigInteger.ZERO;
        }
        BigInteger res = BigInteger.ZERO;
        for (int i = 0; i < bits; i++) {
            if (this._bits.at(offset + i) == 1) {
                res = res.add(BigInteger.ONE.shiftLeft(bits - i - 1));
            }
        }
        return res;
    }

    /**
     * @param bytes  1 byte = 8 bits
     * @param offset offset to start from (bit)
     * @return
     */
    private byte[] _preloadBuffer(int bytes, int offset) {
        // Try to load fast
        byte[] fastBuffer = this._bits.trySubBuffer(offset, bytes * 8);
        if (fastBuffer != null) {
            return fastBuffer;
        }
        // Load slow
        byte[] buf = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            buf[i] = (byte) this._preloadUint(8, offset + i * 8);
        }
        return buf;
    }

    private TonAddress _loadInternalAddress() {
        int type = this._preloadUint(2, this._offset);
        if (type != 2) {
            throw new IllegalArgumentException("Invalid address");
        }
        // No Anycast supported
        if (this._preloadUint(1, this._offset + 2) != 0) {
            throw new IllegalArgumentException("Invalid address");
        }
        // Read address
        int wc = this._preloadUint(8, this._offset + 3);
        byte[] hash = this._preloadBuffer(32, this._offset + 11);
        // Update offset
        this._offset += 267;
        return new TonAddress(wc, hash);
    }

    private Object _loadExternalAddress() {
        throw new UnsupportedOperationException("目前用不上，暂未开发");
    }

}
