package org.xj.commons.web3j.req;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.Web3jConfig;
import org.xj.commons.web3j.Web3jUtil;
import org.xj.commons.web3j.abi.FunctionReturnDecoderPlus;
import org.xj.commons.web3j.enums.MultiCallEnum;
import org.xj.commons.web3j.abi.datatypes.TryResult;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.utils.Numeric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一些方法需要在链上同步连续执行才能获得变化后的值。这个同步连续操作需要multiCall合约完成。
 * 不推荐在一个multiCall合约方法中拼装非常多的方法，因为decode返回值比平常慢。
 * 推荐写法是: 可以异步执行的方法，使用web3 batchRequest查询. 必须同步执行的方法，封装成最小颗粒度的`EthMultiCallReq`放到web3 batchRequest里查询.
 *
 * @author xj
 * @version 1.0.0 createTime:  2023/3/10 10:20
 */
@Slf4j
public class EthMultiCallReq extends EthCallReq {

    private final static String AGGREGATE = "tryAggregate";
    private List<EthCallReq> reqList;
    private List<Type> inputParams;
    private List<TypeReference<?>> outputParams;
    private Function aggregateFunction;

    private DefaultBlockParameter block;

    public EthMultiCallReq(List<EthCallReq> reqList) {
        super(null, null, null);
        if (CollectionUtils.isEmpty(reqList)) {
            return;
        }
        this.reqList = reqList;
        List<ContractPayLoad> contractCallInfos = reqList.stream()
            .map(o -> new ContractPayLoad(new Address(o.getAddress()), FunctionEncoder.encode(o.getFunction())))
            .collect(Collectors.toList());

        this.inputParams = Arrays.asList(new Bool(false), new DynamicArray(ContractPayLoad.class, contractCallInfos));

        this.outputParams = Collections.singletonList(new TypeReference<DynamicArray<TryResult>>() {
        });
        this.aggregateFunction = new Function(AGGREGATE, inputParams, outputParams);

    }

    @Deprecated
    private EthMultiCallReq(String address, String msgSender, Function function) {
        super(null, null, null);
    }

    @Override
    public Request<?, EthCall> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        DefaultBlockParameter block = defaultBlockIfNull(this.block, defaultBlock);
        String chain = Web3jConfig.getChain(web3j);
        MultiCallEnum multiCallEnum = MultiCallEnum.get(chain);
        if (multiCallEnum == null) {
            log.error("未配置multiCall合约，chain:{}", chain);
            return web3j.ethCall(Transaction.createEthCallTransaction("", "", ""), block);
        }
        String encodedFunction = FunctionEncoder.encode(aggregateFunction);
        return web3j.ethCall(Transaction.createEthCallTransaction(null, multiCallEnum.getContract(), encodedFunction), block);
    }

    @Override
    public Object getResult(EthCall response) {
        Web3jUtil.validateResponse(response);
        List<Type> results = FunctionReturnDecoderPlus.decode(response.getValue(), aggregateFunction.getOutputParameters());

        List<Object> ret = new ArrayList<>();
        if (results != null && results.size() > 0) {
            List<TryResult> resultList = (List<TryResult>) results.get(0).getValue();
            for (int i = 0; i < reqList.size(); i++) {
                Function function = reqList.get(i).getFunction();
                TryResult tryResult = resultList.get(i);
                //这个请求失败，快速返回null，进行下一个结果解析
                if (!tryResult.getSuccess().getValue()) {
                    ret.add(null);
                    continue;
                }
                //这个请求成功，解析结果
                List<Type> decodeValues = FunctionReturnDecoderPlus.decode(Numeric.toHexString(tryResult.getData().getValue()),
                    function.getOutputParameters());
                Object typeValue = Web3jUtil.convertTypeToValue(decodeValues);
                ret.add(typeValue);
            }
        }
        //如果结果数量与请求数量不一致，返回null；否则返回ret
        // (说明：需要全部成功时，失败请求没有结果，故数量不一致表示存在失败；允许失败时，失败的请求填充null，故请求与结果的数量也应当是一致的。)
        return ret.size() != reqList.size() ? null : ret;
    }

    private static class ContractPayLoad extends DynamicStruct {

        private Address target;

        private String callData;

        ContractPayLoad(Address target, String callData) {
            super(target, new DynamicBytes(Numeric.hexStringToByteArray(callData)));
            this.target = target;
            this.callData = callData;
        }
    }

}
