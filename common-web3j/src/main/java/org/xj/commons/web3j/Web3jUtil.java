package org.xj.commons.web3j;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.abi.FunctionReturnDecoderPlus;
import org.xj.commons.web3j.enums.ChainTypeEnum;
import org.xj.commons.web3j.req.EthCallReq;
import org.xj.commons.web3j.req.Web3BatchCmd;
import org.xj.commons.web3j.req.Web3Cmd;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.web3j.abi.datatypes.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.*;
import org.web3j.protocol.core.methods.response.EthCall;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xj
 * @since 2022/4/15 15:31
 */
@Slf4j
public class Web3jUtil {

    /* ====================== 多链单例 ==================== */

    /**
     * Web3jUtil客户端地址集合，ChainTypeEnum.code -> 不同链web3jUtil实例
     */
    private static Map<String, Web3jUtil> clientMap = Maps.newConcurrentMap();
    /**
     * web3 BatchRequest api 限制，一次最多发送1000个请求
     */
    public final static int WEB3_BATCH_LIMIT = 1000;

    public static Web3jUtil get(String chain) {
        String chainCode = ChainTypeEnum.explainCode(chain);
        if (clientMap.containsKey(chainCode)) {
            return clientMap.get(chainCode);
        } else {
            Web3jUtil web3jUtil = new Web3jUtil(chainCode, Web3jConfig.get(chainCode));
            clientMap.put(chainCode, web3jUtil);
            return web3jUtil;
        }
    }

    public static Web3jUtil getHeavy(String chain) {
        String chainCode = ChainTypeEnum.explainCode(chain);
        String chainCodeHeavy = chainCode + "-heavy";
        if (clientMap.containsKey(chainCodeHeavy)) {
            return clientMap.get(chainCodeHeavy);
        } else {
            Web3jUtil web3jUtil = new Web3jUtil(chainCode, Web3jConfig.getHeavy(chainCode));
            clientMap.put(chainCodeHeavy, web3jUtil);
            return web3jUtil;
        }
    }

    /* ===================== 工具行为 =====================*/

    private final Web3j web3j;
    private final String chain;

    private Web3jUtil(String chain, Web3j client) {
        this.chain = chain;
        this.web3j = client;
    }

    private Web3j getWeb3j(DefaultBlockParameter block) {
        if (block == DefaultBlockParameterName.LATEST) {
            return web3j;
        } else {
            return Web3jConfig.getHeavy(chain);
        }
    }

    private <T extends Response<?>, Res> Request<?, T> getRequest(Web3Cmd<T, Res> req, DefaultBlockParameter block) {
        Web3j web3jClient = getWeb3j(block);
        return req.getRequest(web3jClient, block);
    }

    /**
     * 调用合约方法，返回结果是复杂结构体
     *
     * @param function        函数
     * @param contractAddress 合约地址
     * @param block           区块
     */
    public List<Type> callReadMethod(Function function, String contractAddress, Long block) {
        return callReadMethod(function, contractAddress, DefaultBlockParameter.valueOf(BigInteger.valueOf(block)));
    }

    public List<Type> callReadMethod(Function function, String contractAddress, DefaultBlockParameter block) {
        return callMethod(function, null, contractAddress, block);
    }

    public List<Object> callReadMethod0(Function function, String contractAddress, DefaultBlockParameter block) {
        List<Type> typeValues = callReadMethod(function, contractAddress, block);
        return typeValues.stream().map(Web3jUtil::getTypeValue).collect(Collectors.toList());
    }

    /**
     * 调用合约方法，返回对象是简单类型
     *
     * @param function        函数
     * @param contractAddress 合约地址
     * @param block           区块
     */
    public Object quickCallReadMethod(Function function, String contractAddress, Long block) {
        return quickCallReadMethod(function, contractAddress, DefaultBlockParameter.valueOf(BigInteger.valueOf(block)));
    }

    public Object quickCallReadMethod(Function function, String contractAddress, DefaultBlockParameter block) {
        List<Type> responseParams = callReadMethod(function, contractAddress, block);
        return convertTypeToValue(responseParams);
    }

    public boolean containsMethod(Function function, String contractAddress, DefaultBlockParameter block) {
        EthCall response = internalCallMethod(function, contractAddress, contractAddress, block);
        List<Type> decode = FunctionReturnDecoderPlus.decode(response.getValue(), function.getOutputParameters());
        //合约有方法，调用成功
        boolean successResult = Objects.nonNull(response.getResult()) && CollectionUtils.isNotEmpty(decode);
        //合约有方法，方法代码内部报错
        boolean errorResult = Objects.nonNull(response.getError()) && Objects.nonNull(response.getError().getData());
        return successResult || errorResult;
    }

    public Object quickCallMethod(Function function, String from, String to, DefaultBlockParameter block) {
        List<Type> responseParams = callMethod(function, from, to, block);
        return convertTypeToValue(responseParams);
    }

    /**
     * 使用批量工具查询
     *
     * @param from     对应参数 {@link EthCallReq#getMsgSender()}
     * @param to       对应参数 {@link EthCallReq#getAddress()}
     * @param function 对应参数 {@link EthCallReq#getFunction()}
     */
    public List<Type> callMethod(Function function, String from, String to, DefaultBlockParameter block) {
        EthCall response = internalCallMethod(function, from, to, block);
        return FunctionReturnDecoderPlus.decode(response.getValue(), function.getOutputParameters());
    }

    private EthCall internalCallMethod(Function function, String from, String to, DefaultBlockParameter block) {

        if (block instanceof DefaultBlockParameterNumber && ((DefaultBlockParameterNumber) block).getBlockNumber() == null) {
            block = DefaultBlockParameterName.LATEST;
        }

        /*
         * String from null(optional)
         * String to 合约地址
         * String data ABI
         */
        EthCall response = null;
        try {
            response = getRequest(new EthCallReq(to, from, function), block).send();
            validateResponse(response);
        } catch (Exception e) {
            // flink任务不打印日志
            throw new RuntimeException("合约调用异常:" + e.getMessage());
        }
        return response;
    }

    /**
     * 调用web3j api
     *
     * @param req   请求
     * @param <T>   req的web3Response泛型
     * @param <Res> req返回泛型
     * @return result
     */
    public <T extends Response<?>, Res> Res call(Web3Cmd<T, Res> req) {
        return call(req, null);
    }

    public <T extends Response<?>, Res> Res call(Web3Cmd<T, Res> req, DefaultBlockParameter block) {
        if (Objects.isNull(req)) {
            return null;
        }
        Request<?, T> request = getRequest(req, block);
        //发送请求
        T response = null;
        try {
            response = request.send();
        } catch (Exception e) {
            // flink任务不打印日志
            RuntimeException newException = new RuntimeException("合约调用异常:" + e.getMessage());
            newException.setStackTrace(e.getStackTrace());
            throw newException;
        }
        return req.getResult(response);
    }

    /**
     * @param reqList 长度超过1000会报错
     * @param block   区块
     * @return
     */
    public List<Object> callBatch(List<? extends Web3BatchCmd<? extends Response<?>, ?>> reqList, DefaultBlockParameter block) {
        if (CollectionUtils.isEmpty(reqList)) {
            return Collections.emptyList();
        }
        //组装 BatchRequest
        BatchRequest batchRequest = getWeb3j(block).newBatch();
        reqList.forEach(req -> batchRequest.add(getRequest(req, block)));
        //发送请求
        BatchResponse batchResponse = null;
        try {
            batchResponse = batchRequest.send();
        } catch (Exception e) {
            // flink任务不打印日志
            throw new RuntimeException("合约调用异常:" + e.getMessage());
        }
        //处理结果
        List<Object> javaObjList = Lists.newArrayList();
        List<? extends Response<?>> responses = batchResponse.getResponses();
        for (int i = 0; i < responses.size(); i++) {
            Web3BatchCmd req = reqList.get(i);
            Response<?> response = responses.get(i);
            Object javaObj = req.getResult(response);
            javaObjList.add(javaObj);
        }
        return javaObjList;
    }

    /**
     * dev环境控台打印错误信息
     *
     * @param response web3查链响应
     */
    public static void validateResponse(EthCall response) {
        if (Web3jConfig.debug) {
            if (StringUtils.isNotEmpty(response.getRevertReason())) {
                System.err.println("web3j >　revert reason:" + response.getRevertReason());
            }
            if (Objects.nonNull(response.getError())) {
                System.err.println("web3j > error msg:" + response.getError().getMessage());
                System.err.println("web3j > error data:" + response.getError().getData());
            }
        }
    }

    public static void validateResponse(Response response) {
        if (Web3jConfig.debug) {
            if (Objects.nonNull(response.getError())) {
                System.err.println("web3j > error msg:" + response.getError().getMessage());
                System.err.println("web3j > error data:" + response.getError().getData());
            }
        }
    }


    public static Object getTypeValue(Object o) {
        if (o instanceof StructType) {
            return o;
        } else if (o instanceof Array) {
            List<? extends Type> value = ((Array<? extends Type>) o).getValue();
            return value.stream().map(Web3jUtil::getTypeValue).collect(Collectors.toList());
        } else if (o instanceof Address) {
            //统一格式：address返回小写
            return StringUtils.lowerCase(((Address) o).getValue());
        } else if (o instanceof Type) {
            return ((Type) o).getValue();
        } else {
            return o;
        }
    }

    public static Object convertTypeToValue(List<Type> decodeValues) {
        //1. 返回的是简单值
        if (!CollectionUtils.isEmpty(decodeValues) && decodeValues.size() == 1) {
            Type functionResult = decodeValues.get(0);
            return Web3jUtil.getTypeValue(functionResult);
        }
        //2. 返回的是复杂对象
        else if (!CollectionUtils.isEmpty(decodeValues) && decodeValues.size() > 1) {
            List<Object> list = decodeValues.stream().map(Web3jUtil::getTypeValue).collect(Collectors.toList());
            return list;
        }
        //3. 返回结果长度为0，目标地址非合约
        else {
            return null;
        }
    }

}
