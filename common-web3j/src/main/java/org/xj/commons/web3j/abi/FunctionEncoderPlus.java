package org.xj.commons.web3j.abi;

import org.xj.commons.web3j.abi.datatypes.MethodIdFunction;
import org.web3j.abi.DefaultFunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/28 13:39
 */
public class FunctionEncoderPlus extends DefaultFunctionEncoder {

    @Override
    public String encodeFunction(final Function function) {
        if (function instanceof MethodIdFunction) {
            final String methodId = ((MethodIdFunction) function).getMethodId();
            final List<Type> parameters = function.getInputParameters();
            return encodeWithSelector(methodId, parameters);
        } else {
            return super.encodeFunction(function);
        }
    }

}
