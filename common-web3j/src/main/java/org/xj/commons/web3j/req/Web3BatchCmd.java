package org.xj.commons.web3j.req;

import org.web3j.protocol.core.Response;

/**
 * @param <T>      Request 泛型
 * @param <Result> Response http响应结果转化后的业务对象泛型 (相当于取result)
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:38
 */
public interface Web3BatchCmd<T extends Response<?>, Result> extends Web3Cmd<T, Result> {

}