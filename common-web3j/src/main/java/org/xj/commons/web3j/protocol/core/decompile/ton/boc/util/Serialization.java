package org.xj.commons.web3j.protocol.core.decompile.ton.boc.util;

import org.xj.commons.toolkit.MathUtil;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.BitReader;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.BitString;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Boc;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/13 17:20
 */
public class Serialization {


    private static int getHashesCount(int levelMask) {
        return getHashesCountFromMask(levelMask & 7);
    }

    private static int getHashesCountFromMask(int mask) {
        int n = 0;
        for (int i = 0; i < 3; i++) {
            n += (mask & 1);
            mask = mask >> 1;
        }
        return n + 1; // 1 repr + up to 3 higher hashes
    }

    private static CellStruct readCell(BitReader reader, int sizeBytes) {

        // D1
        int d1 = reader.loadUint(8);
        int refsCount = d1 % 8;
        boolean exotic = (d1 & 8) != 0;

        // D2
        int d2 = reader.loadUint(8);
        int dataBytesize = MathUtil.ceilDiv2(d2);

        boolean paddingAdded = (d2 % 2) != 0;

        int levelMask = d1 >> 5;
        boolean hasHashes = (d1 & 16) != 0;
        int hash_bytes = 32;

        int hashesSize = hasHashes ? getHashesCount(levelMask) * hash_bytes : 0;
        int depthSize = hasHashes ? getHashesCount(levelMask) * 2 : 0;

        reader.skip(hashesSize * 8);
        reader.skip(depthSize * 8);

        // Bits
        BitString bits = BitString.EMPTY;
        if (dataBytesize > 0) {
            if (paddingAdded) {
                bits = reader.loadPaddedBits(dataBytesize * 8);
            } else {
                bits = reader.loadBits(dataBytesize * 8);
            }
        }

        // Refs
        int[] refs = new int[refsCount];
        for (int i = 0; i < refsCount; i++) {
            refs[i] = reader.loadUint(sizeBytes * 8);
        }

        // Result
        return new CellStruct(
                bits,
                refs,
                exotic
        );
    }

    private static int calcCellSize(Cell cell, int sizeBytes) {
        return 2 /* D1+D2 */ + MathUtil.ceilDiv(cell.getBits().length(), 8) + cell.getRefs().length * sizeBytes;
    }

    public static Boc parseBoc(byte[] src) {
        BitReader reader = new BitReader(new BitString(src, 0, src.length * 8));
        int magic = reader.loadUint(32);
        if (magic == 0x68ff65f3) {
            int size = reader.loadUint(8);
            int offBytes = reader.loadUint(8);
            int cells = reader.loadUint(size * 8);
            int roots = reader.loadUint(size * 8); // Must be 1
            int absent = reader.loadUint(size * 8);
            int totalCellSize = reader.loadUint(offBytes * 8);
            byte[] index = reader.loadBuffer(cells * offBytes);
            byte[] cellData = reader.loadBuffer(totalCellSize);
            return new Boc(
                    size,
                    offBytes,
                    cells,
                    roots,
                    absent,
                    totalCellSize,
                    index,
                    cellData,
                    new int[]{0}
            );
        } else if (magic == 0xacc3a728) {
            int size = reader.loadUint(8);
            int offBytes = reader.loadUint(8);
            int cells = reader.loadUint(size * 8);
            int roots = reader.loadUint(size * 8); // Must be 1
            int absent = reader.loadUint(size * 8);
            int totalCellSize = reader.loadUint(offBytes * 8);
            byte[] index = reader.loadBuffer(cells * offBytes);
            byte[] cellData = reader.loadBuffer(totalCellSize);
            byte[] crc32 = reader.loadBuffer(4);
//            if (!crc32c(src.subarray(0, src.length - 4)).equals(crc32)) {
//                throw Error('Invalid CRC32C');
//            }
            return new Boc(
                    size,
                    offBytes,
                    cells,
                    roots,
                    absent,
                    totalCellSize,
                    index,
                    cellData,
                    new int[]{0}
            );
        } else if (magic == 0xb5ee9c72) {
            int hasIdx = reader.loadUint(1);//--0
            int hasCrc32c = reader.loadUint(1);//--0
            int hasCacheBits = reader.loadUint(1);//--0
            int flags = reader.loadUint(2); // Must be 0
            int size = reader.loadUint(3);//--1
            int offBytes = reader.loadUint(8);//--1
            int cells = reader.loadUint(size * 8);//--1
            int roots = reader.loadUint(size * 8);//--1
            int absent = reader.loadUint(size * 8);//--0
            int totalCellSize = reader.loadUint(offBytes * 8);//--0x5d
            int[] root = new int[roots];
            for (int i = 0; i < roots; i++) {
                root[i] = reader.loadUint(size * 8);//--0
            }
            byte[] index = null;
            if (hasIdx == 1) {
                index = reader.loadBuffer(cells * offBytes);
            }
            byte[] cellData = reader.loadBuffer(totalCellSize);
            if (hasCrc32c == 1) {
                byte[] crc32 = reader.loadBuffer(4);
//                if (!crc32c(src.subarray(0, src.length - 4)).equals(crc32)) {
//                    throw Error('Invalid CRC32C');
//                }
            }
            return new Boc(
                    size,
                    offBytes,
                    cells,
                    roots,
                    absent,
                    totalCellSize,
                    index,
                    cellData,
                    root
            );
        } else {
            throw new IllegalArgumentException("Invalid magic");
        }
    }

    public static Cell[] deserializeBoc(byte[] src) {
        //
        // Parse BOC
        //
        Boc boc = parseBoc(src);
        BitReader reader = new BitReader(new BitString(boc.getCellData(), 0, boc.getCellData().length * 8));
        //
        // Load cells
        //
        CellStruct[] cells = new CellStruct[boc.getCells()];
        for (int i = 0; i < boc.getCells(); i++) {
            CellStruct cll = readCell(reader, boc.getSize());
            cells[i] = cll;
        }
        //
        // Build cells
        //
        for (int i = cells.length - 1; i >= 0; i--) {
            if (cells[i].result != null) {
                throw new RuntimeException("Impossible");
            }
            List<Cell> refs = new ArrayList<>();
            for (int r : cells[i].refs) {
                if (cells[r].result == null) {
                    throw new RuntimeException("Invalid BOC file");
                }
                refs.add(cells[r].result);
            }
            cells[i].result = new Cell(cells[i].bits, refs.toArray(new Cell[0]), cells[i].isExotic);
        }
        //
        // Load roots
        //
        Cell[] roots = new Cell[boc.getRoot().length];
        for (int i = 0; i < boc.getRoot().length; i++) {
            roots[i] = (cells[boc.getRoot()[i]].result);
        }
        //
        // Return
        //
        return roots;
    }

    private static class CellStruct {
        BitString bits;
        int[] refs;
        boolean isExotic;
        Cell result;

        public CellStruct(BitString bits, int[] refs, boolean isExotic) {
            this.bits = bits;
            this.refs = refs;
            this.isExotic = isExotic;
        }
    }

}
