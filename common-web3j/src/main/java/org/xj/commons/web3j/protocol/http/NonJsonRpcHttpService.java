package org.xj.commons.web3j.protocol.http;

import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.protocol.core.NonJsonRpcRequest;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;
import org.web3j.protocol.exceptions.ClientConnectionException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 15:27
 */
public abstract class NonJsonRpcHttpService extends SpecialHttpService {

    public NonJsonRpcHttpService(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url, httpClient, includeRawResponses);
    }


    /**
     * 原方法来自{@link org.web3j.protocol.Service#send(Request, Class)}
     * 修改了
     * ```
     * String payload = objectMapper.writeValueAsString(request)
     * try (InputStream result = performIO(payload))
     * ```
     * 原来的performIO(payload)是post请求jsonrpc的JSONBody,现在改成适用于tron api的方法
     *
     * @param request      request
     * @param responseType 返回体类型
     * @return
     * @throws IOException
     */
    protected <T extends Response> T nonJsonRpcSend(NonJsonRpcRequest request, Class<T> responseType) throws IOException {
        try (InputStream result = this.nonJsonRpcPerformIO(request)) {
            if (result != null) {
                Object o = objectMapper.readValue(result, Object.class);
                //异常返回，只有error
                if (o instanceof Map) {
                    Map map = (Map) o;
                    if (map.size() == 1 && (map.containsKey("Error"))) {
                        T ret = objectMapper.convertValue(Collections.emptyMap(), responseType);
                        Object msg = map.values().iterator().next();
                        ret.setError(new Response.Error(-1, String.valueOf(msg)));
                        return ret;
                    }
                }
                //正常返回的是result对象的json字符，包装一层，适配response结构
                LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
                map.put("result", o);
                return objectMapper.convertValue(map, responseType);
            } else {
                return null;
            }
        }
    }

    /**
     * 原方法来自{@link org.web3j.protocol.http.HttpService#performIO(String)}
     * 修改了构造okhttp3.Request的部分，使它能支持Get和Post
     *
     * @param request request
     * @return
     * @throws IOException
     */
    protected InputStream nonJsonRpcPerformIO(NonJsonRpcRequest request) throws IOException {

        //============================
        //改动部分开始
        Map params = request.getParamsMap();
        String payload = this.objectMapper.writeValueAsString(params);
        String httpMethod = request.getHttpMethod();
        String method = request.getMethod();
        String url = this.nonJsonRpcApiUrl() + "/" + method;

        RequestBody requestBody = StringUtils.equalsIgnoreCase("GET", httpMethod) ? null : RequestBody.create(payload, JSON_MEDIA_TYPE);
        Headers headers = this.buildHeaders();
        okhttp3.Request httpRequest = (new okhttp3.Request.Builder()).url(url).headers(headers)
                .method(httpMethod, requestBody)
                .build();
        //改动部分结束
        //============================
        try (okhttp3.Response response = httpClient().newCall(httpRequest).execute()) {
            processHeaders(response.headers());
            ResponseBody responseBody = response.body();
            if (response.isSuccessful()) {
                if (responseBody != null) {
                    return new ByteArrayInputStream(responseBody.string().getBytes());
                } else {
                    return null;
                }
            } else {
                int code = response.code();
                String text = responseBody == null ? "N/A" : responseBody.string();

                throw new ClientConnectionException(
                        "Invalid response received: " + code + "; " + text);
            }
        }
    }

    private Headers buildHeaders() {
        return Headers.of(super.getHeaders());
    }

    abstract String nonJsonRpcApiUrl();

    abstract OkHttpClient httpClient();

}
