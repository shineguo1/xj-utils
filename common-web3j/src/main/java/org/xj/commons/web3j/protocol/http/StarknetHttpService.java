package org.xj.commons.web3j.protocol.http;

import com.alibaba.fastjson2.JSON;
import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.protocol.core.JsonRpc_Starknet_Web3j;
import org.xj.commons.web3j.protocol.core.method.request.StrkFilter;
import org.xj.commons.web3j.protocol.core.method.response.model.starknet.StrkBlockWithTxHashes;
import org.xj.commons.web3j.protocol.core.method.response.model.starknet.StrkEvent;
import org.xj.commons.web3j.protocol.core.method.response.model.starknet.StrkEvents;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetBlockWithTxHashes;
import org.xj.commons.web3j.protocol.core.method.response.starknet.StrkGetEvents;
import org.xj.commons.web3j.req.starknet.StrkGetEventsReq;
import com.google.common.collect.Lists;
import okhttp3.OkHttpClient;
import org.web3j.protocol.core.*;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.request.Filter;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthLog;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/13 17:09
 */
public class StarknetHttpService extends SpecialHttpService {

    private JsonRpc_Starknet_Web3j thisWeb3j = new JsonRpc_Starknet_Web3j(this);

    public StarknetHttpService(String url, OkHttpClient httpClient, boolean includeRawResponses) {
        super(url, httpClient, includeRawResponses);
    }

    @Override
    public <T extends Response> T send(Request request, Class<T> responseType) throws IOException {
        String method = request.getMethod();
        //特殊方法(适配器)：
        if (method.equalsIgnoreCase("eth_getLogs")) {
            return (T) adaptEthGetLogs((Request<?, EthLog>) request, responseType);
        } else if (method.equalsIgnoreCase("eth_getBlockByNumber")) {
            return (T) adaptEthGetBlockByNumber((Request<?, EthLog>) request, responseType);
        }
        //其他方法
        return super.send(request, responseType);
    }

    /**
     * starknet 返回的batchResponse是乱序的，这里纠正它的顺序，使response和request数组的index对应。
     */
    @Override
    public BatchResponse sendBatch(BatchRequest batchRequest) throws IOException {
        BatchResponse batchResponse = super.sendBatch(batchRequest);
        if (batchResponse == null) {
            return null;
        }
        List<Request<?, ? extends Response<?>>> requests = batchResponse.getRequests();
        List<? extends Response<?>> responses = batchResponse.getResponses();
        if (responses == null || responses.size() == 0) {
            return null;
        }
        Map<Long, ? extends List<? extends Response<?>>> responsesMap =
                responses.stream().collect(Collectors.groupingBy(Response::getId, Collectors.toList()));
        List<Response<?>> sortedResponses = new ArrayList<>();
        for (Request<?, ? extends Response<?>> request : requests) {
            long id = request.getId();
            List<? extends Response<?>> list = responsesMap.get(id);
            if (list != null && list.size() > 0) {
                Response<?> remove = list.remove(0);
                sortedResponses.add(remove);
            }
        }
        return new BatchResponse(requests, sortedResponses);
    }

    private EthBlock adaptEthGetBlockByNumber(Request<?, EthLog> request, Class responseType) throws IOException {
        List<?> params = request.getParams();
        DefaultBlockParameter defaultBlockParameter = (DefaultBlockParameter) params.get(0);
        StrkGetBlockWithTxHashes resp = thisWeb3j.strkGetBlockWithTxHashes(defaultBlockParameter).send();

        if (resp.hasError()) {
            EthBlock ethBlock = new EthBlock();
            ethBlock.setError(resp.getError());
            return ethBlock;
        }
        StrkBlockWithTxHashes result = resp.getResult();
        EthBlock.Block block = new EthBlock.Block();
        block.setNumber(String.valueOf(result.getBlockNumber()));
        block.setHash(result.getBlockHash());
        block.setParentHash(result.getParentHash());
        List<String> transactions = result.getTransactions();
        block.setTransactions(CollectionUtils.toStream(transactions).map(EthBlock.TransactionHash::new).collect(Collectors.toList()));
        block.setTimestamp(String.valueOf(result.getTimestamp()));
        // 如果是null会报错
        block.setBaseFeePerGas("0x0");
        block.setDifficulty("0x0");
        block.setTotalDifficulty("0x0");
        block.setGasLimit("0x0");
        block.setGasUsed("0x0");
        block.setNonce("0x0");

        EthBlock ethBlock = new EthBlock();
        ethBlock.setResult(block);
        return ethBlock;
    }

    /**
     * ethGetLog需要
     */
    private EthLog adaptEthGetLogs(Request<?, EthLog> request, Class responseType) throws IOException {
        List<?> params = request.getParams();
        if (params == null || params.size() == 0) {
            return null;
        }
        EthFilter ethFilter = (EthFilter) params.get(0);
        List<String> addresses = ethFilter.getAddress();
        DefaultBlockParameter fromBlock = ethFilter.getFromBlock();
        DefaultBlockParameter toBlock = ethFilter.getToBlock();
        List<Filter.FilterTopic> filterTopics = ethFilter.getTopics();
        List<String> topics = Lists.newArrayList();
        for (Filter.FilterTopic filterTopic : filterTopics) {
            if (filterTopic instanceof Filter.SingleTopic) {
                addSingleTopicToList(topics, (Filter.SingleTopic) filterTopic);
            } else if (filterTopic instanceof Filter.ListTopic) {
                List<Filter.SingleTopic> singleTopics = ((Filter.ListTopic) filterTopic).getValue();
                singleTopics.forEach(singleTopic -> addSingleTopicToList(topics, singleTopic));
            }
        }

        // 节点批量查询
        // 如果没有监听合约地址，添加一条 starknet_getEvents jsonRpc 方法
        // 如果有监听的合约地址，有几条地址，添加几条 starknet_getEvents jsonRpc 方法
        BatchRequest batchRequest = new BatchRequest(this);
        if (addresses == null || addresses.size() == 0) {
            Request<?, StrkGetEvents> req = new StrkGetEventsReq(
                    new StrkFilter()
                            .setFromBlock(fromBlock)
                            .setToBlock(toBlock)
                            .addOptionalTopics(topics)
            ).getRequest(thisWeb3j, null);
            batchRequest.add(req);
        } else {
            for (String address : addresses) {
                Request<?, StrkGetEvents> req = new StrkGetEventsReq(
                        new StrkFilter()
                                .setAddress(address)
                                .setFromBlock(fromBlock)
                                .setToBlock(toBlock)
                                .addOptionalTopics(topics)
                ).getRequest(thisWeb3j, null);
                batchRequest.add(req);
            }
        }
        BatchResponse batchResponse = super.sendBatch(batchRequest);
        List<? extends Response<?>> responses = batchResponse.getResponses();

        //处理结果，如果发现任何一个错误，返回结果就设置为这个错误, 否则拼装成EthLog
        List<EthLog.LogResult> logList = Lists.newArrayList();
        for (Response<?> response : responses) {
            StrkGetEvents resp = (StrkGetEvents) response;
            //如果发现一个错误，返回结果就设置为这个错误
            if (resp.hasError()) {
                EthLog log = new EthLog();
                log.setError(resp.getError());
                return log;
            }
            //通过后错误检查后，处理正常结果
            StrkEvents strkEvents = resp.getResult();
            for (StrkEvent strkEvent : strkEvents.getEvents()) {
                EthLog.LogObject logObject = convertStrkEventToEthLogObject(strkEvent);
                logList.add(logObject);
            }
        }
        EthLog log = new EthLog();
        log.setResult(logList);
        return log;
    }

    private EthLog.LogObject convertStrkEventToEthLogObject(StrkEvent strkEvent) {
        List<String> data = strkEvent.getData();
        List<String> keys = strkEvent.getKeys();
        BigInteger blockNumber = strkEvent.getBlockNumber();
        String blockHash = strkEvent.getBlockHash();
        String contractAddress = strkEvent.getFromAddress();
        String transactionHash = strkEvent.getTransactionHash();

        EthLog.LogObject logObject = new EthLog.LogObject();
        logObject.setAddress(contractAddress);
        logObject.setData(JSON.toJSONString(data));
        logObject.setTopics(keys);
        logObject.setBlockHash(blockHash);
        logObject.setBlockHash(blockHash);
        logObject.setBlockNumber(blockNumber == null ? null : String.valueOf(blockNumber));
        logObject.setTransactionHash(transactionHash);
        return logObject;
    }

    private void addSingleTopicToList(List<String> list, Filter.SingleTopic singleTopic) {
        list.add(singleTopic.getValue());
    }

}
