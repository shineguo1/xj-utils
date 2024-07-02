package org.xj.commons.web3j.protocol.http;

import okhttp3.OkHttpClient;
import org.web3j.protocol.http.HttpService;

/**
 * 自定义Http web3jService模板，需要实现
 *
 * @author xj
 * @version 1.0.0 createTime:  2023/10/24 20:42
 */
public abstract class SpecialHttpService extends HttpService {

    public SpecialHttpService(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url, httpClient, includeRawResponses);

    }
}
