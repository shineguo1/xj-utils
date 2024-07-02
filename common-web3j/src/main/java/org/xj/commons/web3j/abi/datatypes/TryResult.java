package org.xj.commons.web3j.abi.datatypes;

import lombok.Data;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/11/3 10:28
 */
@Data
public class TryResult extends DynamicStruct {

    private Bool success;
    private DynamicBytes data;


    public TryResult(Bool success, DynamicBytes data) {
        super(success, data);
        this.success = success;
        this.data = data;
    }

}
