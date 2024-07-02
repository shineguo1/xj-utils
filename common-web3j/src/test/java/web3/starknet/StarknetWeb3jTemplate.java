package web3.starknet;

import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.protocol.core.JsonRpc_Starknet_Web3j;
import org.xj.commons.web3j.protocol.http.StarknetHttpService;
import com.google.common.collect.ImmutableMap;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 10:53
 */
public class StarknetWeb3jTemplate {

    private static void InitWeb3Env() {
        ;
        Web3jConfig.addEnv("Starknet", "http://172.20.192.14:30014/nodeserver/starknet");
    }

    static {
        init();
        InitWeb3Env();
    }

    public static void init() {
        Web3jConfig.setClientTypeMap(ImmutableMap.of(
                "Starknet", JsonRpc_Starknet_Web3j.class));
        Web3jConfig.setWeb3jServiceTypeMap(ImmutableMap.of(
                "Starknet", StarknetHttpService.class));


        //控制台打印开关
        Web3jConfig.debug = true;
    }

}
