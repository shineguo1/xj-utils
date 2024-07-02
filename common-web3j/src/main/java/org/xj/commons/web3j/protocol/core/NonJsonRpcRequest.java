package org.xj.commons.web3j.protocol.core;

import lombok.Getter;
import lombok.Setter;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 非evm api JsonRpc请求体，即朴素的GET、POST请求
 *
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 16:24
 */
public class NonJsonRpcRequest<S, T extends Response> extends Request<S, T> {

    /**
     * post, get
     */
    @Setter
    @Getter
    protected String httpMethod;

    protected Map<String, Object> params;

    public NonJsonRpcRequest() {
    }

    /**
     * 说明：
     *
     * @param method       拼接在请求路径末尾，诸如https://toncenter.com/api/v2/{method}
     * @param params       作为请求的JSONBody
     * @param web3jService 发送请求的执行器
     * @param type         返回参数类型
     * @param httpMethod   指明是post方法，还是get方法
     */

    public NonJsonRpcRequest(String method, Map<String, Object> params, Web3jService web3jService, Class<T> type, String httpMethod) {
        super(method, null, web3jService, type);
        this.params = params;
        this.httpMethod = httpMethod == null ? null : httpMethod.toUpperCase(Locale.ROOT);
    }

    @Override
    public List<S> getParams() {
        throw new UnsupportedOperationException();
    }

    public Map<String, Object> getParamsMap() {
        return this.params;
    }
}
