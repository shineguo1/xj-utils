package org.xj.commons.web3j.api;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.BatchMergeStrategy;
import org.xj.commons.web3j.BatchUtils;
import org.xj.commons.web3j.enums.ProxyTypeEnum;
import org.xj.commons.web3j.protocol.core.decompile.Decompiler;
import org.xj.commons.web3j.req.EthGetCodeReq;
import com.google.common.collect.Sets;
import org.web3j.protocol.core.DefaultBlockParameter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 14:48
 */
public class InternalContractApi {
    /**
     * 合约和代理合约的 methodCode
     * 开销：(代理合约层数+2)次查链
     *
     * @param chain    链
     * @param contract 合约
     * @param block    区块
     * @return
     */
    public static Set<String> getFullMethodCodes(String chain, String contract, DefaultBlockParameter block) {
        Set<String> contracts = deepProxyAddress(chain, contract, block);
        contracts.add(contract);
        List<EthGetCodeReq> getBtyeCodeReqList = CollectionUtils.toStream(contracts).map(EthGetCodeReq::new).collect(Collectors.toList());
        List<String> res = (List) BatchUtils.get(chain).batch(getBtyeCodeReqList, false, block);
        return CollectionUtils.toStream(res).filter(Objects::nonNull)
                .flatMap(byteCode -> CollectionUtils.toStream(Decompiler.parseMethodCode(byteCode)))
                .collect(Collectors.toSet());
    }

    /**
     * @param contract 原合约
     * @return 代理合约、以及代理合约的代理合约
     */
    public static Set<String> deepProxyAddress(String chain, String contract, DefaultBlockParameter block) {
        Set<String> proxyAddresses = proxyAddress(chain, contract, block);

        //循环体，初始化为contract的直接代理地址
        Set<String> nonRepeatedAddresses = proxyAddresses;

        int loop = 1, limit = 10;
        while (CollectionUtils.isNotEmpty(nonRepeatedAddresses) && loop <= limit) {
            //代理地址的非重复代理地址
            nonRepeatedAddresses = nonRepeatedAddresses.stream()
                    //查代理的代理
                    .map(o -> proxyAddress(chain, o, block))
                    //合并查询结果
                    .reduce(CollectionUtils::merge)
                    //去除已收录结果，避免循环引用的环路导致死循环（以防万一）
                    .filter(o -> !proxyAddresses.contains(o))
                    .orElseGet(Sets::newHashSet);
            CollectionUtils.addAll(proxyAddresses, nonRepeatedAddresses);
            loop++;
        }
        return proxyAddresses;
    }


    /**
     * @param contract 原合约
     * @return 代理合约
     */
    private static Set<String> proxyAddress(String chain, String contract, DefaultBlockParameter block) {
        List<BatchMergeStrategy<String>> strategies = Arrays.stream(ProxyTypeEnum.values())
                .map(o -> o.proxyAddressStrategy(contract))
                .filter(Objects::nonNull).collect(Collectors.toList());
        BatchMergeStrategy.execute(chain, strategies, block);
        return CollectionUtils.toStream(strategies).map(BatchMergeStrategy::getResult)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


}
