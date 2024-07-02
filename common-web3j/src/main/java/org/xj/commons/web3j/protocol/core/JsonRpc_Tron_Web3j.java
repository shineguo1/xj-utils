package org.xj.commons.web3j.protocol.core;

import org.xj.commons.web3j.protocol.core.method.response.TronGetAccount;
import org.xj.commons.web3j.protocol.core.method.response.TronGetAssetIssue;
import com.google.common.collect.ImmutableMap;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

import java.util.concurrent.ScheduledExecutorService;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:24
 */
public class JsonRpc_Tron_Web3j extends JsonRpc2_0Web3j {

    public JsonRpc_Tron_Web3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpc_Tron_Web3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }

    //
    public TronRequest<?, TronGetAccount> tronGetAccount(String address, Boolean visible) {
        return new TronRequest<>(
                "getaccount",
                ImmutableMap.of("address", address,
                        "visible", visible),
                web3jService,
                TronGetAccount.class,
                "post");
    }

    public TronRequest<?, TronGetAssetIssue> tronGetAssetIssueById(Long tokenId) {
        return new TronRequest<>(
                "getassetissuebyid",
                ImmutableMap.of("value", tokenId),
                web3jService,
                TronGetAssetIssue.class,
                "post");
    }
}
