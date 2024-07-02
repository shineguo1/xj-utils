### 2023-10-26
- 更新了tron api， tron api暂时没发现批量调用方法。 如果需要用tron api，地址配成“{eth_api_url},{tron_api_url}”, 如果只用eth api照旧配置即可。
- 例如"http://127.0.0.1:8001/eth_rpc,http://127.0.0.1:8001/tron_rpc"
- 然后加上下面这段代码配置
```
@Configuration
public class Web3jConfiguration {
    static {
        init();
    }
    
    public static void init() {
        //配置特殊的web3j客户端实现类
        Web3jConfig.setClientTypeMap(ImmutableMap.of("Tron", JsonRpc_Tron_Web3j.class));
        //配置特殊的web3j_http服务实现类
        Web3jConfig.setWeb3jServiceTypeMap(ImmutableMap.of("Tron", TronHttpService.class));
    }
}
```
- 使用示例如下, 目前只实现了tron api的`/wallet/getaccount`接口, 并且返回体只实现了部分参数。需要用什么接口再联系我。
```
DefaultBlockParameter blockParameter = DefaultBlockParameterName.LATEST;
//tron api接口
Object ret = Web3jUtil.get("Tron").call(new TronGetAccountReq("TPyjyZfsYaXStgz2NmAraF1uZcMtkgNan5", true));
//eth api接口
Object ret2 = Web3jUtil.get("Tron").call(new EthGetBalanceReq("0xf0cc5a2a84cd0f68ed1667070934542d673acbd8").withBlock(blockParameter));
```