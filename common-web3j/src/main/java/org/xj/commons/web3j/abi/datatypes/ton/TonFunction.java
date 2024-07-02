package org.xj.commons.web3j.abi.datatypes.ton;

import com.alibaba.fastjson2.TypeReference;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 14:12
 */
@AllArgsConstructor
@Data
public class TonFunction<DECODE> {

    private String name;
    private List<String> inputParams;
    private TypeReference<DECODE> outputParams;
}
