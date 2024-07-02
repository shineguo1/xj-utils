package org.xj.commons.web3j.protocol.core.decompile.ton.boc;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/13 17:39
 */
@Data
@AllArgsConstructor
public class Boc {

    private int size;
    private int offBytes;
    private int cells;
    /**
     * root数量
     */
    private int roots;
    private int absent;
    private int totalCellSize;
    private byte[] index;
    private byte[] cellData;
    /**
     * 记录root index的数组
     */
    private int[] root;

}
