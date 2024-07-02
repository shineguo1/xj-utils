package org.xj.commons.web3j.protocol.core.decompile;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/6/10 9:48
 */
@Data
@AllArgsConstructor
public class Op {

    Op(String name, Integer fee, Integer in, Integer out, Boolean dynamic) {
        this(name, fee, in, out, dynamic, null);
    }

    private String name;
    private Integer fee;
    private Integer in;
    private Integer out;
    private Boolean dynamic;
    private Boolean async;
    /**
     * hex string
     */
//    private String pushData;
}
