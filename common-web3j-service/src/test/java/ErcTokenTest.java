import org.redisson.config.Config;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.xj.commons.redis.client.RedisManager;
import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.api.InternalContractApi;
import org.xj.commons.web3j.api.TokenTool;
import org.xj.commons.web3j.constant.FunctionConstant;
import org.xj.commons.web3j.enums.ChainTypeEnum;

import java.util.Set;

import static org.web3j.utils.Numeric.hexStringToByteArray;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 10:14
 */
public class ErcTokenTest {

    static {
        //web3j
        //--------------------------
        //节点配置
        Web3jConfig.addEnv(ChainTypeEnum.ETH.getCode(), "ETH_RPC_URL");
        Web3jConfig.addEnv(ChainTypeEnum.BSC.getCode(), "BSC_RPC_URL");
        Web3jConfig.addEnv(ChainTypeEnum.ARB.getCode(), "ARB_RPC_URL");
        //控制台打印开关
        Web3jConfig.debug = false;
        //连接池、线程池配置, 有默认配置，可重写
//        Web3jProperties.get().setAsyncBatchPool(null);
//        Web3jProperties.get().setOkHttpMaxRequests(null);
//        Web3jProperties.get().setOkHttpMaxRequestsPerHost(null);

        //redis
        //--------------------------
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        //redis配置
        RedisManager.get().setClient(config);
    }


    public static void main(String[] args) {
        //[0x000000000cd2b9dccf0d5672cd577c982856b6a6]
        Function function = FunctionConstant.supportsInterface(hexStringToByteArray("0x80ac58cd"));
        String encode = FunctionEncoder.encode(function);
        System.out.println(encode);

        String chain = "Ethereum";
        test("0xabe292b291a18699b09608de86888d77ad6baf23", chain);
//
        System.out.println("ERC20:");
        test("0x514910771af9ca656af840dff83e8264ecf986ca", chain);
        test("0x4a220e6096b25eadb88358cb44068a3248254675", chain);
        test("0x6e1A19F235bE7ED8E3369eF73b196C07257494DE", chain);
        test("0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48", chain);
        test("0xae7ab96520de3a18e5e111b5eaab095312d7fe84", chain);
//        System.out.println("ERC721:");
//        test("0xa8Ad3151f6226eED6fa8F7238A833684f0a86FCd", chain);
//        test("0x2ea0D46d78cf57e1288cF8582FECd89cF3f078f6", chain);
//        test("0x57f1887a8bf19b14fc0df6fd9b2acc9af147ea85", chain);
//        test("0xAEF3e1B29C6898E0Ca9e2F53D0e40dBb2Bd82192", chain);
//        test("0x7fd1e9F69a0c69F8013Db1718b85473f57C4e908", chain);
//        System.out.println("ERC1155:");
//        test("0x495f947276749ce646f68ac8c248420045cb7b5e", chain);
//        test("0x01cecf7a8f0095fa622ff03d714fd2244548da35", chain);
//        test("0xc36cf0cfcb5d905b8b513860db0cfe63f6cf9f5c", chain);
//        test("0xff2dedc3a4542988a86e1f97437280949e3342a7", chain);
//        test("0x7daec605e9e2a1717326eedfd660601e2753a057", chain);
//        System.out.println("UNKNOW:");
//        test("0xf400706bb17762c4a40431ee69540d2e4962c536", chain);
//        test("0xb3f52194aef662b5806be62feb54d32f94435b3d", chain);
    }

    private static void printProxyContracts(String contract) {
        Set<String> ret1 = InternalContractApi.deepProxyAddress("Ethereum", contract, DefaultBlockParameterName.LATEST);
        System.out.println(ret1);
    }

    private static void test(String token, String chain) {
//        Set<String> methodCodes = ContractTool.getFullMethodCodes("BSC", token, DefaultBlockParameterName.LATEST);
        boolean erc20 = TokenTool.isErc20(chain, token, DefaultBlockParameterName.LATEST);
        boolean erc721 = TokenTool.isErc721(chain, token, DefaultBlockParameterName.LATEST);
        System.out.println(erc20 + " " + erc721);
    }

}
