package web3.starknet;

import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.enums.ChainTypeEnum;
import org.xj.commons.web3j.event.core.EventDecoder;
import com.google.common.collect.Lists;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import web3.starknet.event.Mint;
import web3.starknet.event.Transfer;
import web3.starknet.event.Transfer2;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 9:34
 */
public class Test_Strk_ethLogs extends StarknetWeb3jTemplate {


    public static void main(String[] args) throws IOException {
        test_ethGetLogs();
    }

    static void test_ethGetLogs() throws IOException {
        Web3j web3j = Web3jConfig.get(ChainTypeEnum.Starknet.getCode());
        EthFilter ethFilter = new EthFilter(
                new DefaultBlockParameterNumber(608551),
                new DefaultBlockParameterNumber(608551),
                Arrays.asList("0x049d36570d4e46f48e99674bd3fcc84644ddd6b96f7c741b1562b82f9e004dc7",
                        "0x07c2e1e733f28daa23e78be3a4f6c724c0ab06af65f6a95b5e0545215f1abc1b")
        );
        ethFilter.addOptionalTopics(
                "0x99cd8bde557814842a3121e8ddfd433a539b8c9f14bf31ebf108d12e6196e9",
                "0x34e55c1cd55f1338241b50d352f0e91c7e4ffad0e4271d64eb347589ebdfd16"
        );
        EthLog ethLog = web3j.ethGetLogs(ethFilter).send();
        List<EthLog.LogResult> results = ethLog.getResult();
        System.out.println(results);
        List<Transfer> transferList = Lists.newArrayList();
        List<Transfer2> transfer2List = Lists.newArrayList();
        List<Mint> mintList = Lists.newArrayList();

        EventDecoder<Mint> mintDecoder = EventDecoder.create(Mint.class);
        EventDecoder<Transfer> transferDecoder = EventDecoder.create(Transfer.class);
        EventDecoder<Transfer2> transfer2Decoder = EventDecoder.create(Transfer2.class);
        for (EthLog.LogResult result : results) {
            EthLog.LogObject logObj = (EthLog.LogObject) result;
            if (isEvent(logObj, "0x7c2e1e733f28daa23e78be3a4f6c724c0ab06af65f6a95b5e0545215f1abc1b", mintDecoder)) {
                mintList.add(mintDecoder.decode(logObj));
            } else if (isEvent(logObj, "0x7c2e1e733f28daa23e78be3a4f6c724c0ab06af65f6a95b5e0545215f1abc1b", transferDecoder)) {
                transferList.add(transferDecoder.decode(logObj));
            } else if (isEvent(logObj, "0x49d36570d4e46f48e99674bd3fcc84644ddd6b96f7c741b1562b82f9e004dc7", transfer2Decoder)) {
                transfer2List.add(transfer2Decoder.decode(logObj));
            }
        }
        System.out.println();

    }

    private static boolean isEvent(EthLog.LogObject logObj, String contract, EventDecoder decoder) {
        return logObj.getAddress().equalsIgnoreCase(contract)
                && logObj.getTopics().get(0).equalsIgnoreCase(decoder.getSignature());
    }


}
