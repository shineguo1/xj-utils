package web3.starknet;

import com.alibaba.fastjson2.JSON;
import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.Web3jUtil;
import org.xj.commons.web3j.protocol.core.JsonRpc_Starknet_Web3j;
import org.xj.commons.web3j.protocol.core.method.response.model.starknet.StrkBlockWithTxHashes;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetBlockWithTxHashes;
import org.xj.commons.web3j.req.EthGetBlockReq;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 9:34
 */
public class Test_Strk_GetBlock extends StarknetWeb3jTemplate {


    public static void main(String[] args) throws IOException {
        test_adapt_getEthBlock();
    }

    static void test_starknet_getBlockWithTxHashes() throws IOException {
        JsonRpc_Starknet_Web3j web3j = (JsonRpc_Starknet_Web3j) Web3jConfig.get("Starknet");
        StrkGetBlockWithTxHashes resp = web3j.strkGetBlockWithTxHashes(DefaultBlockParameterName.LATEST).send();
        StrkBlockWithTxHashes result = resp.getResult();
        System.out.println(JSON.toJSONString(result));

        StrkGetBlockWithTxHashes resp2 = web3j.strkGetBlockWithTxHashes(DefaultBlockParameter.valueOf(BigInteger.valueOf(612345))).send();
        StrkBlockWithTxHashes result2 = resp2.getResult();
        System.out.println(JSON.toJSONString(result2));
    }


    static void test_adapt_getEthBlock() throws IOException {
        EthBlock.Block result = Web3jUtil.get("Starknet").call(new EthGetBlockReq(DefaultBlockParameterName.LATEST));
        System.out.println(JSON.toJSONString(result));

        EthBlock.Block result2 = Web3jUtil.get("Starknet").call(new EthGetBlockReq(BigInteger.valueOf(612345)));
        System.out.println(JSON.toJSONString(result2));
    }


}
