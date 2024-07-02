package org.xj.commons.web3j.abi.datatypes;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;

import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/9 14:08
 */
public class RawData implements Type {

    // =================== 实例方法 ================

    private String data;

    public RawData(String data) {
        this.data = data;
    }

    @Override
    public Object getValue() {
        return data;
    }

    @Override
    public String getTypeAsString() {
        return null;
    }

    // =================== 静态方法 =================

    private final static List<TypeReference<?>> OUTPUT_PARAMETERS = Collections.singletonList(TypeReference.create(RawData.class));

    /**
     * RawData仅用于outputParameters返回原始data密文。所以固定写法，与FunctionReturnDecoderPlus.decode的处理逻辑照应。
     */
    public static List<TypeReference<?>> outputParameters() {
        return OUTPUT_PARAMETERS;
    }
}
