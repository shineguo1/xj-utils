package org.xj.commons.web3j;

import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.config.Web3jProperties;
import org.xj.commons.web3j.enums.ChainTypeEnum;
import org.xj.commons.web3j.protocol.core.JsonRpc2Expand_Web3j;
import org.xj.commons.web3j.protocol.http.SpecialHttpService;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * web3j配置
 *
 * @author xj
 * @date 2022/3/1
 */
@Slf4j
public class Web3jConfig {
    public static final Class<JsonRpc2Expand_Web3j> DEFAULT_CLIENT_CLASS = JsonRpc2Expand_Web3j.class;
    public static final Class<HttpService> DEFAULT_WEB3JSERVICE_CLASS = HttpService.class;
    /**
     * web3j客户端地址集合，ChainTypeEnum.code[-后缀] -> address
     */
    private static Map<String, String> clientAddressMap = Maps.newHashMap();

    /**
     * web3j客户端实现类集合，ChainTypeEnum.code -> web3j
     */
    private static Map<String, Class<? extends Web3j>> clientTypeMap = Maps.newHashMap();
    /**
     * web3jService实现类, ChainTypeEnum.code -> web3jService
     */
    private static Map<String, Class<? extends Web3jService>> web3jServiceTypeMap = Maps.newHashMap();

    @Getter
    private static Map<String, Integer> batchRequestLimitMap = Maps.newHashMap();
    /**
     * web3j客户端地址集合，ChainTypeEnum.code[-后缀] -> web3j client
     */
    private static Map<String, Web3j> clientMap = Maps.newConcurrentMap();
    private static Map<Web3j, String> clientInvertedMap = Maps.newConcurrentMap();

    /**
     * 控制台打印开关
     */
    public static boolean debug = false;

    /**
     * 添加节点环境
     *
     * @param chain 链
     * @param rpc   节点url
     */
    public static void addEnv(String chain, String rpc) {
        clientAddressMap.put(chain, rpc);
    }

    public static void addBatchLimit(String chain, int limit) {
        batchRequestLimitMap.put(chain, limit);
    }

    static {
        Web3jConfig.addBatchLimit(ChainTypeEnum.AURORA.getCode(), 99);
        Web3jConfig.addBatchLimit(ChainTypeEnum.CRO.getCode(), 50);
        Web3jConfig.addBatchLimit(ChainTypeEnum.OKC.getCode(), 100);
    }

    /**
     * 注入节点环境
     *
     * @param map 全量节点环境map
     */
    public static void setClientAddressMap(Map<String, String> map) {
        clientAddressMap = map;
    }

    public static void setWeb3jServiceTypeMap(Map<String, Class<? extends SpecialHttpService>> map) {
        web3jServiceTypeMap = (Map) map;
    }

    public static void setClientTypeMap(Map<String, Class<? extends Web3j>> map) {
        clientTypeMap = map;
    }

    private static boolean contains(String chain) {
        return clientAddressMap.containsKey(chain);
    }

    public static Web3j getHeavy(String chain) {
        String heavyCode = chain + "-heavy";
        if (contains(heavyCode)) {
            return getWeb3j(heavyCode);
        } else {
            return getWeb3j(chain);
        }
    }

    public static Web3j getRetry(String chain) {
        String retryCode = chain + "-retry";
        if (contains(retryCode)) {
            return getWeb3j(retryCode);
        } else {
            return getWeb3j(chain);
        }
    }

    public static Web3j get(String chain) {
        return getWeb3j(chain);
    }

    public static String getChain(Web3j web3j) {
        String chainPropertiesKey = clientInvertedMap.getOrDefault(web3j, "");
        String[] chainFields = chainPropertiesKey.split("-");
        return chainFields[0];
    }

    public static <T extends Web3j> T build(Class<T> clientClazz, String clientAddress) {
        return build(clientClazz, DEFAULT_WEB3JSERVICE_CLASS, clientAddress);
    }

    public static <T extends Web3j> T build(Class<T> clientClazz, Class<? extends Web3jService> serviceClazz, String clientAddress) {
        if (StringUtils.isEmpty(clientAddress)) {
            return null;
        }
        try {
            Constructor<T> constructor = clientClazz.getConstructor(Web3jService.class);
            Object[] constructorArgs = {buildService(clientAddress, serviceClazz)};
            return constructor.newInstance(constructorArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Web3jService buildService(String clientAddress, Class<? extends Web3jService> serviceClazz) {
        Web3jService web3jService;
        if (!StringUtils.isEmpty(clientAddress)) {
            try {
                Constructor<? extends Web3jService> constructor = serviceClazz.getConstructor(String.class, OkHttpClient.class, boolean.class);
                Object[] constructorArgs = {clientAddress, createOkHttpClient(), false};
                return constructor.newInstance(constructorArgs);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            web3jService = new HttpService(createOkHttpClient());
        }
        return web3jService;
    }

    private static OkHttpClient createOkHttpClient() {
        //构造分发器：使用默认线程池；自定义连接池参数，将host并发数限制改为与最大并发数一致（取消host限制）
        Dispatcher dispatcher = new Dispatcher();
        if (Objects.nonNull(Web3jProperties.get().getOkHttpMaxRequests())) {
            dispatcher.setMaxRequests(Web3jProperties.get().getOkHttpMaxRequests());
        }
        if (Objects.nonNull(Web3jProperties.get().getOkHttpMaxRequestsPerHost())) {
            dispatcher.setMaxRequestsPerHost(Web3jProperties.get().getOkHttpMaxRequestsPerHost());
        }
        //构造client
        Builder builder = new Builder();
        return builder.connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build();
    }

    /**
     * @param chain ChainTypeEnum.code[-后缀]
     */
    private static synchronized Web3j init(@NotNull String chain) {
        if (Objects.nonNull(clientMap.get(chain))) {
            return clientMap.get(chain);
        }
        // 实例化web3j
        String address = clientAddressMap.get(chain);
        String directChainCode = chain.split("-")[0];
        Web3j web3j = build(directChainCode, address);
        if (Objects.nonNull(web3j)) {
            clientMap.put(chain, web3j);
            clientInvertedMap.put(web3j, chain);
        }
        return web3j;
    }

    @Nullable
    public static Web3j build(String directChainCode, String address) {
        Class<? extends Web3j> clientClass = clientTypeMap.getOrDefault(directChainCode, DEFAULT_CLIENT_CLASS);
        Class<? extends Web3jService> web3jServiceClass = web3jServiceTypeMap.getOrDefault(directChainCode, DEFAULT_WEB3JSERVICE_CLASS);
        return build(clientClass, web3jServiceClass, address);
    }

    /**
     * @param chain ChainTypeEnum.code[-后缀]
     */
    private static Web3j getWeb3j(String chain) {
        // 获取web3
        Web3j web3j = clientMap.get(chain);
        return Objects.isNull(web3j) ? init(chain) : web3j;
    }
}
