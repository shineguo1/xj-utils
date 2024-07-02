package org.xj.commons.web3j.req;

import org.xj.commons.toolkit.ObjectUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.*;

/**
 * @param <T> Request 泛型
 * @param <Result> Response http响应结果转化后的业务对象泛型 (相当于取result)
 * @author xj
 * @version 1.0.0 createTime:  2023/2/13 10:38
 */
public interface Web3Cmd<T extends Response<?>, Result> {

    /**
     * 对象适配为{@link Request} api
     *
     * @param web3j        web3j client
     * @param defaultBlock 查询需要区块号，但新建对象没有传入区块时，使用的缺省值
     * @return Web3j的Request api
     */
    Request<?, T> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock);

    /**
     * {@link Response} 将response从包装对象转为值对象
     *
     * @param response web3j的返回体
     * @return 值对象
     */
    Result getResult(T response);

    /**
     * 如果obj区块为空，返回缺省值
     *
     * @param obj          区块
     * @param defaultBlock 缺省值
     * @return 确保返回非空区块
     */
    default DefaultBlockParameter defaultBlockIfNull(DefaultBlockParameter obj, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter ret = ObjectUtils.defaultIfNull(obj, defaultBlock);
        if (ret instanceof DefaultBlockParameterNumber && ((DefaultBlockParameterNumber) ret).getBlockNumber() == null) {
            ret = DefaultBlockParameterName.LATEST;
        }
        return ret;
    }

}