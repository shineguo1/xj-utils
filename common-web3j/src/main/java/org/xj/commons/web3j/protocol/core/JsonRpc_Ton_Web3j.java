package org.xj.commons.web3j.protocol.core;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.protocol.core.method.response.ton.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.core.JsonRpc2_0Web3j;

import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 接口兼容，但不支持evm api
 *
 * @author xj
 * @version 1.0.0 createTime:  2022/10/12 15:24
 */
public class JsonRpc_Ton_Web3j extends JsonRpc2_0Web3j {

    public JsonRpc_Ton_Web3j(Web3jService web3jService) {
        super(web3jService);
    }

    public JsonRpc_Ton_Web3j(Web3jService web3jService, long pollingInterval, ScheduledExecutorService scheduledExecutorService) {
        super(web3jService, pollingInterval, scheduledExecutorService);
    }


    public TonRequest<?, TonGetJetton> tonGetJetton(String jettonAddress) {
        return new TonRequest<>(
                String.format("jettons/%s", jettonAddress),
                null,
                web3jService,
                TonGetJetton.class,
                "get");
    }

    public TonRequest<?, TonGetAccountNfts> tonGetAccountNfts(
            @NotNull String account,
            @Nullable String collection,
            @Nullable Integer limit,
            @Nullable Integer offset,
            @Nullable Boolean indirectOwnership
    ) {
        StringBuilder url = new StringBuilder(String.format("accounts/%s/nfts?collection=%s", account, collection == null ? "" : collection));
        if (Objects.nonNull(limit)) {
            url.append("&limit=").append(limit);
        }
        if (Objects.nonNull(offset)) {
            url.append("&offset=").append(offset);
        }
        if (Objects.nonNull(indirectOwnership)) {
            url.append("&indirect_ownership=").append(indirectOwnership);
        }
        return new TonRequest<>(
                url.toString(),
                null,
                web3jService,
                TonGetAccountNfts.class,
                "get");
    }

    public TonRequest<?, TonGetAccountJettons> tonGetAccountJettons(String account, String currencies) {
        return new TonRequest<>(
                String.format("accounts/%s/jettons?currencies=%s", account, currencies),
                null,
                web3jService,
                TonGetAccountJettons.class,
                "get");
    }

    public TonRequest<?, TonGetAccount> tonGetAccount(String account) {
        return new TonRequest<>(
                String.format("accounts/%s", account),
                null,
                web3jService,
                TonGetAccount.class,
                "get");
    }


    public TonRequest<?, TonConsoleGetMethods> tonGetAccountMethod(String account, String methodName, Collection<String> args) {
        StringBuilder url = new StringBuilder(String.format("/blockchain/accounts/%s/methods/%s", account, methodName));
        if (CollectionUtils.isNotEmpty(args)) {
            int i = 0;
            for (String arg : args) {
                url.append(i++ == 0 ? "?" : "&")
                        .append("args=")
                        .append(arg);
            }
        }
        return new TonRequest<>(
                url.toString(),
                null,
                web3jService,
                TonConsoleGetMethods.class,
                "get");
    }

    public TonRequest<?, TonGetNftCollection> tonGetNftCollection(String collectionAddress) {
        return new TonRequest<>(
                String.format("nfts/collections/%s", collectionAddress),
                null,
                web3jService,
                TonGetNftCollection.class,
                "get");
    }
}
