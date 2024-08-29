package org.xj.commons.web3j.req;

import lombok.Data;
import org.web3j.protocol.core.Response;

/**
 * @author xinjie_guo
 * @version 1.0.0 createTime:  2024/8/29 14:09
 */
@Data
public abstract class BaseWeb3Cmd<T extends Response<?>, Result> implements Web3Cmd<T, Result> {

    protected T response;

    public Result handleResponse(T response) {
        this.response = response;
        return getResult(response);
    }


}
