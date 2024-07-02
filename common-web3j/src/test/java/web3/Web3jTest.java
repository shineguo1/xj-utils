package web3;

import com.google.common.collect.ImmutableMap;
import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.enums.ChainTypeEnum;
import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.JsonRpc_Tron_Web3j;
import org.xj.commons.web3j.protocol.http.TonHttpService;
import org.xj.commons.web3j.protocol.http.TronHttpService;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 10:14
 */
public class Web3jTest {

    private static void InitWeb3Env() {
        Web3jConfig.addEnv("Ethereum", "YOUR_RPC_URL");
        Web3jConfig.addEnv("BSC-heavy", "YOUR_RPC_URL");
        Web3jConfig.addEnv("BSC", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Fantom", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Gnosis", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Polygon-heavy", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Polygon", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Avalanche", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Arbitrum", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Cronos", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Starknet", "YOUR_RPC_URL");
        Web3jConfig.addEnv("MyChain", "YOUR_RPC_URL");
        Web3jConfig.addEnv("Ton", "http://tonapi.io/v2");
        Web3jConfig.addEnv(ChainTypeEnum.TRON.getCode(), "ETH_API_RPC_URL,TRON_API_RPC_URL");
    }

    static {
        init();
        InitWeb3Env();
    }

    public static void init() {
        Web3jConfig.setClientTypeMap(ImmutableMap.of(
                "Tron", JsonRpc_Tron_Web3j.class,
                "Ton", JsonRpc_Ton_Web3j.class));
        Web3jConfig.setWeb3jServiceTypeMap(ImmutableMap.of(
                "Tron", TronHttpService.class,
                "Ton", TonHttpService.class));


        //控制台打印开关
        Web3jConfig.debug = true;
        //@Override 连接池、线程池配置
//        Web3jProperties.get().setAsyncBatchPool(null);
//        Web3jProperties.get().setOkHttpMaxRequests(null);
//        Web3jProperties.get().setOkHttpMaxRequestsPerHost(null);
    }


}
