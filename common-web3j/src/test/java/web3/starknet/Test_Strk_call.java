package web3.starknet;

import com.alibaba.fastjson2.JSON;
import org.xj.commons.web3j.BatchUtils;
import org.xj.commons.web3j.abi.datatypes.Address252;
import org.xj.commons.web3j.abi.datatypes.Uint252;
import org.xj.commons.web3j.constant.FunctionConstant;
import org.xj.commons.web3j.req.starknet.StrkCallReq;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import web3.starknet.model.UserAccountData;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 9:34
 */
public class Test_Strk_call extends StarknetWeb3jTemplate {


    public static void main(String[] args) throws IOException {
        test_call();
    }

    static void test_call() throws IOException {
        String user = "0x5c11517f37343d9caf3507d6441a9bcdc51bb020ab9c61b88f326e53fae508c";
        String dWBTC = "0x491480f21299223b9ce770f23a2c383437f9fbf57abc2ac952e9af8cdb12c97";
        String cdpManager = "0x73f6addc9339de9822cab4dac8c9431779c09077f02ba7bc36904ea342dd9eb";
        List<StrkCallReq> reqList = Arrays.asList(
                new StrkCallReq(cdpManager,
                        new Function("getUserAccountData",
                                Collections.singletonList(new Address252(user)),
                                Collections.singletonList(TypeReference.create(UserAccountData.class)))
                ),
                new StrkCallReq(dWBTC, FunctionConstant.NAME),
                new StrkCallReq(dWBTC, FunctionConstant.SYMBOL),
                new StrkCallReq(dWBTC, new Function("decimals",
                        Collections.emptyList(),
                        Collections.singletonList(new TypeReference<Uint252>() {
                        })))
        );
        List<Object> resList = BatchUtils.get("Starknet").batch(reqList, false,
                DefaultBlockParameterName.LATEST);
        System.out.println(JSON.toJSONString(resList));


        List<Object> resList2 = BatchUtils.get("Starknet").batch(reqList, false,
                DefaultBlockParameter.valueOf(BigInteger.valueOf(611432)));
        System.out.println(JSON.toJSONString(resList2));
    }


}
