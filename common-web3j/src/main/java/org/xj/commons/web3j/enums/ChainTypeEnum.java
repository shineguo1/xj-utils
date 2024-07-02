package org.xj.commons.web3j.enums;


import org.xj.commons.toolkit.StringUtils;
import com.google.common.collect.Sets;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @author xj
 * @date on 2021/12/7 17:16
 */

@AllArgsConstructor
public enum ChainTypeEnum {

    /**
     * 以太链
     */
    ETH("Ethereum", "ETH", Sets.newHashSet("Ethereum", "ETH")),

    /**
     * SOL
     */
    SOL("Solana", "SOL", Sets.newHashSet("Solana", "SOL")),

    /**
     * 币安链
     */
    BSC("BSC", "BSC", Sets.newHashSet("BSC", "BNB")),

    /**
     * 币安链
     */
    Pol("Polygon", "POL", Sets.newHashSet("Polygon", "Pol", "Matic")),

    /**
     * Arbitrum链
     */
    ARB("Arbitrum", "ARB", Sets.newHashSet("Arbitrum", "ARB")),
    /**
     * Avalanche链
     */
    AVAX("Avalanche", "AVAX", Sets.newHashSet("Avalanche", "AVAX")),
    /**
     * Fantom 链
     */
    FTM("Fantom", "FTM", Sets.newHashSet("Fantom", "FTM")),
    /**
     * Gnosis 链
     */
    XDAI("Gnosis", "XDAI", Sets.newHashSet("Gnosis", "XDAI")),
    /**
     * Optimism 链
     */
    OP("Optimism", "OP", Sets.newHashSet("Optimism", "OP")),
    /**
     * Cronos 链
     */
    CRO("Cronos", "CRO", Sets.newHashSet("Cronos", "CRO")),
    /**
     * Klaytn 链
     */
    KLAY("Klaytn", "KLAY", Sets.newHashSet("Klaytn", "KLAY")),
    /**
     * Celo 链
     */
    CELO("Celo", "CELO", Sets.newHashSet("Celo")),
    /**
     * Celo 链
     */
    AURORA("Aurora", "AURORA", Sets.newHashSet("Aurora", "AURORA")),

    /**
     * Canto 链
     */
    CANTO("Canto", "CANTO", Sets.newHashSet("Canto")),
    METIS("Metis", "METIS", Sets.newHashSet("Metis")),

    /**
     * Moonbeam 链
     */
    MBEAM("Moonbeam", "MOBM", Sets.newHashSet("Moonbeam", "MOBM")),
    PZE("Polygon zkEVM", "PZE", Sets.newHashSet("Polygon zkEVM", "PZE", "Polygon_zkEVM", "polyzk")),
    KCC("KCC","KCC",Sets.newHashSet("KCC")),

    BTC("BitCoin","BTC",Sets.newHashSet("BTC","BitCoin")),
    LINEA("Linea","LINEA",Sets.newHashSet("Linea")),
    HECO("HECO","HECO",Sets.newHashSet("HECO")),
    MANTLE("Mantle","MNT",Sets.newHashSet("MNT","Mantle")),
    BASE("Base","BASE",Sets.newHashSet("Base")),
    OKC("OKC", "OKT", Sets.newHashSet("OKC", "OKT")),
    CFX("Conflux", "CFX", Sets.newHashSet("Conflux", "CFX")),

    /**
     * Moonriver 链
     */
    MOVR("Moonriver", "MOVR", Sets.newHashSet("Moonriver", "MOVR")),
    TRON("Tron", "TRON", Sets.newHashSet("TRON")),
    Starknet("Starknet", "STRK", Sets.newHashSet("Starknet","STRK")),
    TON("Ton", "TON", Sets.newHashSet("TON")),

    SUI("Sui","SUI",Sets.newHashSet("Sui"));
    @Getter
    private final String code;
    /**
     * 缩写，用作数据表后缀
     */
    @Getter
    private final String abbr;

    @Getter
    private final Set<String> nickNames;

    public static ChainTypeEnum explain(String nickName) {
        if (StringUtils.isEmpty(nickName)) {
            return null;
        }
        for (ChainTypeEnum typeEnum : ChainTypeEnum.values()) {
            if (StringUtils.equalsIgnoreCase(nickName, typeEnum.getCode()) ||
                    StringUtils.equalsIgnoreCase(nickName, typeEnum.getAbbr())) {
                return typeEnum;
            }
        }
        return null;
    }

    public static String explainCode(String nickName) {
        ChainTypeEnum type = explain(nickName);
        return Objects.isNull(type) ? nickName : type.getCode();
    }

    /**
     * @param inputValue     输入值
     * @param inputFieldFunc 与输入值比较的字段
     * @return 返回枚举类对象
     */
    public static ChainTypeEnum explain(String inputValue, Function<ChainTypeEnum, String> inputFieldFunc) {
        if (StringUtils.isEmpty(inputValue)) {
            return null;
        }

        for (ChainTypeEnum typeEnum : ChainTypeEnum.values()) {
            String enumValue = inputFieldFunc.apply(typeEnum);
            if (StringUtils.equalsIgnoreCase(enumValue, inputValue)) {
                return typeEnum;
            }
        }
        return null;
    }

    public static <T> T explain(String inputValue, Function<ChainTypeEnum, String> inputFieldFunc, Function<ChainTypeEnum, T> outputFieldFunc) {
        if (StringUtils.isEmpty(inputValue)) {
            return null;
        }

        for (ChainTypeEnum typeEnum : ChainTypeEnum.values()) {
            String enumValue = inputFieldFunc.apply(typeEnum);
            if (StringUtils.equalsIgnoreCase(enumValue, inputValue)) {
                return outputFieldFunc.apply(typeEnum);
            }
        }
        return null;
    }

    /**
     * 输入缩写，返回code。
     * 输入 "all" 或者 null， 返回null
     *
     * @param nickName
     * @return code
     */
    public static String adaptAbbr(String nickName) {
        if (StringUtils.isEmpty(nickName) || StringUtils.equalsIgnoreCase("all", nickName)) {
            return null;
        }
        ChainTypeEnum chainTypeEnum = ChainTypeEnum.explainByNickName(nickName);
        if (Objects.isNull(chainTypeEnum)) {
            throw new RuntimeException("The current chain is not supported temporarily");
        } else {
            return chainTypeEnum.getCode();
        }
    }

    public static ChainTypeEnum explainByNickName(String nickName) {
        if (StringUtils.isEmpty(nickName)) {
            return null;
        }
        for (ChainTypeEnum typeEnum : ChainTypeEnum.values()) {
            for (String oneNickName : typeEnum.getNickNames()) {
                if (StringUtils.equalsIgnoreCase(oneNickName, nickName)) {
                    return typeEnum;
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.println(adaptAbbr("matic"));
    }
}
