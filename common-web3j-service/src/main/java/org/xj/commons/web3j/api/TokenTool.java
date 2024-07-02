package org.xj.commons.web3j.api;

import org.web3j.protocol.core.DefaultBlockParameter;

import java.util.Collection;
import java.util.Set;


/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 13:56
 */
public class TokenTool {

    public static boolean isErc20(String chain, String contract, DefaultBlockParameter block) {
        Set<String> fullMethodCodes = ContractTool.getFullMethodCodes(chain, contract, block);
        return InternalTokenApi.isErc20(fullMethodCodes);
    }

    public static boolean isErc20(Collection<String> methodCodes) {
        return InternalTokenApi.isErc20(methodCodes);
    }

    public static boolean isErc721(String chain, String contract, DefaultBlockParameter block) {
        Set<String> fullMethodCodes = ContractTool.getFullMethodCodes(chain, contract, block);
        return InternalTokenApi.isErc721(fullMethodCodes);
    }

    public static boolean isErc721(Collection<String> methodCodes) {
        return InternalTokenApi.isErc721(methodCodes);
    }

}
