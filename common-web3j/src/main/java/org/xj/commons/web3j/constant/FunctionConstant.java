package org.xj.commons.web3j.constant;

import com.google.common.collect.Lists;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.Bytes4;
import org.web3j.abi.datatypes.generated.Uint256;

import java.util.Collections;

/**
 * @author xj
 * @date 2022/4/15 15:21
 */
public class FunctionConstant {

    /**
     * {
     * "inputs": [{
     * "internalType": "bytes4",
     * "name": "interfaceId",
     * "type": "bytes4"
     * }],
     * "name": "supportsInterface",
     * "outputs": [{
     * "internalType": "bool",
     * "name": "",
     * "type": "bool"
     * }],
     * "stateMutability": "view",
     * "type": "function"
     * }
     */
    public static Function supportsInterface(byte[] hash) {
        return new Function("supportsInterface",
                Collections.singletonList(new Bytes4(hash)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }

    /**
     * uint256 balanceOf(address)
     */
    public static Function balanceOf(String account) {
        return new Function("balanceOf",
                Collections.singletonList(new Address(account)),
                Collections.singletonList(new TypeReference<Uint>() {
                }));
    }

    /**
     * address ownerOf(uint256)
     */
    public static Function ownerOf(long tokenId) {
        return new Function("ownerOf",
                Collections.singletonList(new Uint256(tokenId)),
                Collections.singletonList(new TypeReference<Address>() {
                }));
    }

    /**
     * bool transfer(address,uint256)
     */
    public static Function transfer(String recipient, Long amount) {
        return new Function("transfer",
                Lists.newArrayList(new Address(recipient), new Uint256(amount)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }

    /**
     * bool transferFrom(address,address,uint256)
     */
    public static Function transferFrom(String sender, String recipient, Long amount) {
        return new Function("transferFrom",
                Lists.newArrayList(new Address(sender), new Address(recipient), new Uint256(amount)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }

    /**
     * bool approve(address,uint256)
     */
    public static Function approve(String spender, Long amount) {
        return new Function("approve",
                Lists.newArrayList(new Address(spender), new Uint256(amount)),
                Collections.singletonList(new TypeReference<Bool>() {
                }));
    }

    /**
     * Uint256 allowance(address,address)
     */
    public static Function allowance(String owner, String spender) {
        return new Function("allowance",
                Lists.newArrayList(new Address(owner), new Address(spender)),
                Collections.singletonList(new TypeReference<Uint256>() {
                }));
    }

    public static final Function DECIMALS = new Function("decimals",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Uint>() {
            }));

    public static final Function SYMBOL = new Function("symbol",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Utf8String>() {
            }));

    public static final Function NAME = new Function("name",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Utf8String>() {
            }));

    public static final Function TOTAL_SUPPLY = new Function("totalSupply",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Uint256>() {
            }));

    public static final Function IMPLEMENTATION = new Function("implementation",
            Collections.emptyList(),
            Collections.singletonList(new TypeReference<Address>() {
            }));

}
