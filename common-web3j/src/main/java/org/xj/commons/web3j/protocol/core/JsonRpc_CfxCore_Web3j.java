package org.xj.commons.web3j.protocol.core;

import org.xj.commons.web3j.protocol.core.method.response.EthGetBlockReceipts;
import io.reactivex.Flowable;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.ShhFilter;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.protocol.core.methods.response.admin.AdminDataDir;
import org.web3j.protocol.core.methods.response.admin.AdminNodeInfo;
import org.web3j.protocol.core.methods.response.admin.AdminPeers;
import org.web3j.protocol.websocket.events.LogNotification;
import org.web3j.protocol.websocket.events.NewHeadsNotification;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:24
 */
public class JsonRpc_CfxCore_Web3j extends JsonRpc2Expand_Web3j {

    public JsonRpc_CfxCore_Web3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpc_CfxCore_Web3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }

    @Override
    public Request<?, EthGetBlockReceipts> ethGetBlockReceipts(
        DefaultBlockParameter defaultBlockParameter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, Web3ClientVersion> web3ClientVersion() {
        return new Request<>(
            "cfx_clientVersion",
            Collections.<String>emptyList(),
            web3jService,
            Web3ClientVersion.class);
    }

    @Override
    public Request<?, Web3Sha3> web3Sha3(String data) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, NetVersion> netVersion() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, NetListening> netListening() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, NetPeerCount> netPeerCount() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, AdminNodeInfo> adminNodeInfo() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, AdminPeers> adminPeers() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, BooleanResponse> adminAddPeer(String url) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, BooleanResponse> adminRemovePeer(String url) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, AdminDataDir> adminDataDir() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthProtocolVersion> ethProtocolVersion() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthChainId> ethChainId() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthCoinbase> ethCoinbase() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthSyncing> ethSyncing() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthMining> ethMining() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthHashrate> ethHashrate() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGasPrice> ethGasPrice() {
        return new Request<>(
            "cfx_gasPrice", Collections.<String>emptyList(), web3jService, EthGasPrice.class);
    }

    @Override
    public Request<?, EthMaxPriorityFeePerGas> ethMaxPriorityFeePerGas() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthFeeHistory> ethFeeHistory(int blockCount, DefaultBlockParameter newestBlock, List<Double> rewardPercentiles) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthAccounts> ethAccounts() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthBlockNumber> ethBlockNumber() {
        return new Request<>(
            "cfx_gasPrice",
            Collections.<String>emptyList(),
            web3jService,
            EthBlockNumber.class);
    }

    @Override
    public Request<?, EthGetBalance> ethGetBalance(String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
            "cfx_getBalance",
            Arrays.asList(address, convertBlockValue(defaultBlockParameter)),
            web3jService,
            EthGetBalance.class);
    }

    @Override
    public Request<?, EthGetStorageAt> ethGetStorageAt(String address, BigInteger position, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
            "cfx_getStorageAt",
            Arrays.asList(
                address,
                Numeric.encodeQuantity(position),
                convertBlockValue(defaultBlockParameter)),
            web3jService,
            EthGetStorageAt.class);
    }

    @Override
    public Request<?, EthGetTransactionCount> ethGetTransactionCount(String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
            "cfx_getNextNonce",
            Arrays.asList(address, convertBlockValue(defaultBlockParameter)),
            web3jService,
            EthGetTransactionCount.class);
    }

    @Override
    public Request<?, EthGetBlockTransactionCountByHash> ethGetBlockTransactionCountByHash(String blockHash) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetBlockTransactionCountByNumber> ethGetBlockTransactionCountByNumber(DefaultBlockParameter defaultBlockParameter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetUncleCountByBlockHash> ethGetUncleCountByBlockHash(String blockHash) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetUncleCountByBlockNumber> ethGetUncleCountByBlockNumber(DefaultBlockParameter defaultBlockParameter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetCode> ethGetCode(String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
            "cfx_getCode",
            Arrays.asList(address, convertBlockValue(defaultBlockParameter)),
            web3jService,
            EthGetCode.class);
    }

    @Override
    public Request<?, EthSign> ethSign(String address, String sha3HashOfDataToSign) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthSendTransaction> ethSendTransaction(Transaction transaction) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthSendTransaction> ethSendRawTransaction(String signedTransactionData) {
        return new Request<>(
            "cfx_sendRawTransaction",
            Arrays.asList(signedTransactionData),
            web3jService,
            EthSendTransaction.class);
    }

    @Override
    public Request<?, EthCall> ethCall(Transaction transaction, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
            "cfx_call",
            Arrays.asList(transaction, convertBlockValue(defaultBlockParameter)),
            web3jService,
            EthCall.class);
    }

    @Override
    public Request<?, EthEstimateGas> ethEstimateGas(Transaction transaction) {
        return new Request<>(
            "cfx_estimateGasAndCollateral", Arrays.asList(transaction), web3jService, EthEstimateGas.class);
    }

    @Override
    public Request<?, EthBlock> ethGetBlockByHash(String blockHash, boolean returnFullTransactionObjects) {
        return new Request<>(
            "cfx_getBlockByHash",
            Arrays.asList(blockHash, returnFullTransactionObjects),
            web3jService,
            EthBlock.class);
    }

    @Override
    public Request<?, EthBlock> ethGetBlockByNumber(DefaultBlockParameter defaultBlockParameter, boolean returnFullTransactionObjects) {
        return new Request<>(
            "cfx_getBlockByEpochNumber",
            Arrays.asList(convertBlockValue(defaultBlockParameter), returnFullTransactionObjects),
            web3jService,
            EthBlock.class);
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByHash(String transactionHash) {
        return new Request<>(
            "cfx_getTransactionByHash",
            Arrays.asList(transactionHash),
            web3jService,
            EthTransaction.class);
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByBlockHashAndIndex(String blockHash, BigInteger transactionIndex) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthTransaction> ethGetTransactionByBlockNumberAndIndex(DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetTransactionReceipt> ethGetTransactionReceipt(String transactionHash) {
        return new Request<>(
            "cfx_getTransactionReceipt",
            Arrays.asList(transactionHash),
            web3jService,
            EthGetTransactionReceipt.class);
    }

    @Override
    public Request<?, EthBlock> ethGetUncleByBlockHashAndIndex(String blockHash, BigInteger transactionIndex) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthBlock> ethGetUncleByBlockNumberAndIndex(DefaultBlockParameter defaultBlockParameter, BigInteger transactionIndex) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthGetCompilers> ethGetCompilers() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthCompileLLL> ethCompileLLL(String sourceCode) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthCompileSolidity> ethCompileSolidity(String sourceCode) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthCompileSerpent> ethCompileSerpent(String sourceCode) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthFilter> ethNewFilter(org.web3j.protocol.core.methods.request.EthFilter ethFilter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthFilter> ethNewBlockFilter() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthFilter> ethNewPendingTransactionFilter() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthUninstallFilter> ethUninstallFilter(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthLog> ethGetFilterChanges(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthLog> ethGetFilterLogs(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthLog> ethGetLogs(org.web3j.protocol.core.methods.request.EthFilter ethFilter) {
        return new Request<>("cfx_getLogs", Arrays.asList(ethFilter), web3jService, EthLog.class);
    }

    @Override
    public Request<?, EthGetWork> ethGetWork() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthSubmitWork> ethSubmitWork(String nonce, String headerPowHash, String mixDigest) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, EthSubmitHashrate> ethSubmitHashrate(String hashrate, String clientId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, DbPutString> dbPutString(String databaseName, String keyName, String stringToStore) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, DbGetString> dbGetString(String databaseName, String keyName) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, DbPutHex> dbPutHex(String databaseName, String keyName, String dataToStore) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, DbGetHex> dbGetHex(String databaseName, String keyName) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhPost> shhPost(org.web3j.protocol.core.methods.request.ShhPost shhPost) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhVersion> shhVersion() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhNewIdentity> shhNewIdentity() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhHasIdentity> shhHasIdentity(String identityAddress) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhNewGroup> shhNewGroup() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhAddToGroup> shhAddToGroup(String identityAddress) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhNewFilter> shhNewFilter(ShhFilter shhFilter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhUninstallFilter> shhUninstallFilter(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhMessages> shhGetFilterChanges(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, ShhMessages> shhGetMessages(BigInteger filterId) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Request<?, TxPoolStatus> txPoolStatus() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<Log> ethLogFlowable(org.web3j.protocol.core.methods.request.EthFilter ethFilter) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<String> ethBlockHashFlowable() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<String> ethPendingTransactionHashFlowable() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<org.web3j.protocol.core.methods.response.Transaction> transactionFlowable() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<org.web3j.protocol.core.methods.response.Transaction> pendingTransactionFlowable() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> blockFlowable(boolean fullTransactionObjects) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock, boolean fullTransactionObjects) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock, boolean fullTransactionObjects, boolean ascending) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(DefaultBlockParameter startBlock, boolean fullTransactionObjects, Flowable<EthBlock> onCompleteFlowable) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> replayPastBlocksFlowable(DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<org.web3j.protocol.core.methods.response.Transaction> replayPastTransactionsFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<org.web3j.protocol.core.methods.response.Transaction> replayPastTransactionsFlowable(DefaultBlockParameter startBlock) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<EthBlock> replayPastAndFutureBlocksFlowable(DefaultBlockParameter startBlock, boolean fullTransactionObjects) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<org.web3j.protocol.core.methods.response.Transaction> replayPastAndFutureTransactionsFlowable(DefaultBlockParameter startBlock) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<NewHeadsNotification> newHeadsNotifications() {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    @Override
    public Flowable<LogNotification> logsNotifications(List<String> addresses, List<String> topics) {
        throw new UnsupportedOperationException("cfx does not support this method");
    }

    private String convertBlockValue(DefaultBlockParameter defaultBlockParameter) {
        if (DefaultBlockParameterName.LATEST.equals(defaultBlockParameter)) {
            return "latest_state";
        } else if (DefaultBlockParameterName.FINALIZED.equals(defaultBlockParameter)) {
            return "latest_finalized";
        } else if (DefaultBlockParameterName.EARLIEST.equals(defaultBlockParameter)) {
            return "earliest";
        } else if (DefaultBlockParameterName.PENDING.equals(defaultBlockParameter)) {
            return "latest_mined";
        } else if (DefaultBlockParameterName.SAFE.equals(defaultBlockParameter)) {
            return "latest_confirmed";
        } else if (DefaultBlockParameterName.ACCEPTED.equals(defaultBlockParameter)) {
            return "latest_confirmed";
        } else {
            return defaultBlockParameter.getValue();
        }
    }

}
