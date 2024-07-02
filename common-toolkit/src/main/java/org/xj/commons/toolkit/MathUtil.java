package org.xj.commons.toolkit;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 9:49
 */
public class MathUtil {

    public static int floorDiv(int a, int b) {
        return Math.floorDiv(a, b);
    }


    public static int ceilDiv(int a, int b) {
        int c = a % b == 0 ? 0 : 1;
        return Math.floorDiv(a, b) + c;
    }

    /**
     * 等价于ceilDiv(a, 2)
     */
    public static int ceilDiv2(int a) {
        return (a >> 1) + (a & 1);
    }
}
