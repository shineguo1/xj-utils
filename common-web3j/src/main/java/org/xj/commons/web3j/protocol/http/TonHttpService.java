package org.xj.commons.web3j.protocol.http;

import org.xj.commons.web3j.protocol.core.TonRequest;
import com.fasterxml.jackson.databind.MapperFeature;
import okhttp3.OkHttpClient;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;

import java.io.IOException;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/10/24 17:31
 */
public class TonHttpService extends NonJsonRpcHttpService {

    private final String tonConsoleApiUrl;
    private final OkHttpClient httpClient;

    public TonHttpService(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url, httpClient, includeRawResponses);
        this.tonConsoleApiUrl = url.trim();
        this.httpClient = httpClient;
        /*
         tron api会返回蛇形，如`net_window_size`, 驼峰，如`assetV2`，还有两种混杂的，如`free_asset_net_usageV2`，所以使用SNAKE_CASE模式会让非标准的蛇形（驼峰、混杂）字段取不到值。
         所以实体对象各个字段的json格式交由实体对象自己定义。
         */
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Override
    public <T extends Response> T send(Request request, Class<T> responseType) throws IOException {
        if (request instanceof TonRequest) {
            //tron api，使用改写的tronSend方法
            return nonJsonRpcSend((TonRequest) request, responseType);
        } else {
            //evm api, 使用原有的send方法
            throw new UnsupportedOperationException("TonApi only support type `TonRequest`");
        }
    }


    @Override
    String nonJsonRpcApiUrl() {
        return tonConsoleApiUrl;
    }

    @Override
    OkHttpClient httpClient() {
        return httpClient;
    }

}
