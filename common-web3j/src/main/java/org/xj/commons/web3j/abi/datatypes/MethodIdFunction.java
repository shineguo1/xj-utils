package org.xj.commons.web3j.abi.datatypes;

import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/28 14:21
 */
public class MethodIdFunction extends Function {
    private String methodId;

    public MethodIdFunction(String methodId, List<Type> inputParameters, List<TypeReference<?>> outputParameters) {
        super(null, inputParameters, outputParameters);
        this.methodId = methodId;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("IncompleteFunction.class not support `Field name`!");
    }

    public String getMethodId() {
        return methodId;
    }

}
