package org.xj.commons.web3j.protocol.http;

import org.xj.commons.web3j.protocol.core.TronRequest;
import com.fasterxml.jackson.databind.MapperFeature;
import okhttp3.OkHttpClient;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.Response;

import java.io.IOException;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/10/24 17:31
 */
public class TronHttpService extends NonJsonRpcHttpService {

    private final String tronApiUrl;
    private final String ethApiUrl;
    private final OkHttpClient httpClient;

    public TronHttpService(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url.split(",")[0].trim(), httpClient, includeRawResponses);
        String[] urls = url.split(",");
        this.ethApiUrl = urls[0].trim();
        this.tronApiUrl = urls[1].trim();
        this.httpClient = httpClient;
        /*
         tron api会返回蛇形，如`net_window_size`, 驼峰，如`assetV2`，还有两种混杂的，如`free_asset_net_usageV2`，所以使用SNAKE_CASE模式会让非标准的蛇形（驼峰、混杂）字段取不到值。
         所以实体对象各个字段的json格式交由实体对象自己定义。
         */
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    @Override
    public <T extends Response> T send(Request request, Class<T> responseType) throws IOException {
        if (request instanceof TronRequest) {
            //tron api，使用改写的tronSend方法
            return nonJsonRpcSend((TronRequest) request, responseType);
        } else {
            //evm api, 使用原有的send方法
            return super.send(request, responseType);
        }
    }

    @Override
    String nonJsonRpcApiUrl() {
        return tronApiUrl;
    }

    @Override
    OkHttpClient httpClient() {
        return httpClient;
    }

}
