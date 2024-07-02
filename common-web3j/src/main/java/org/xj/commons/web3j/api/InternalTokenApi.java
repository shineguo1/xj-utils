package org.xj.commons.web3j.api;

import org.xj.commons.web3j.constant.FunctionConstant;
import org.web3j.abi.FunctionEncoder;

import java.util.Collection;


/**
 * @author xj
 * @version 1.0.0 createTime:  2023/3/15 13:56
 */
public class InternalTokenApi {

    public static final String TOTAL_SUPPLY = FunctionEncoder.encode(FunctionConstant.TOTAL_SUPPLY);
    public static final String ALLOWANCE = FunctionEncoder.encode(FunctionConstant.allowance("0x0", "0x0")).substring(0, 10);
    public static final String BALANCE_OF = FunctionEncoder.encode(FunctionConstant.balanceOf("0x0")).substring(0, 10);
    public static final String TRANSFER = FunctionEncoder.encode(FunctionConstant.transfer("0x0", 0L)).substring(0, 10);
    public static final String TRANSFER_FROM = FunctionEncoder.encode(FunctionConstant.transferFrom("0x0", "0x0", 0L)).substring(0, 10);
    public static final String APPROVE = FunctionEncoder.encode(FunctionConstant.approve("0x0", 0L)).substring(0, 10);
    public static final String OWNER_OF = FunctionEncoder.encode(FunctionConstant.ownerOf(0L)).substring(0, 10);

    public static boolean isErc20(Collection<String> methodCodes) {
        return methodCodes.contains(TOTAL_SUPPLY) &&
//                methodCodes.contains(ALLOWANCE) &&
                methodCodes.contains(BALANCE_OF) &&
                methodCodes.contains(TRANSFER) &&
                methodCodes.contains(TRANSFER_FROM) &&
                methodCodes.contains(APPROVE);
    }



    /**
     * Erc721检验方法：
     * step1：如果合约有 supportsInterface 方法，校验 supportsInterface(0x80ac58cd)
     * step2：检验是否包含 balanceOf(address)、ownerOf(uint256)、transfer(address,uint256)、transferFrom(address,address,uint256、'approve(address,uint256) 方法
     * step3: 如果前两步有任意一个true，表示是ERC721
     */
    public static boolean isErc721(Collection<String> methodCodes) {
        boolean isErc721 = false;
        //2023/03/14 删除supportsInterface判断，直接用codes判断，减少查链
        boolean containsAnyOfTransfer = methodCodes.contains(TRANSFER) || methodCodes.contains(TRANSFER_FROM);
        isErc721 = methodCodes.contains(BALANCE_OF) &&
                methodCodes.contains(OWNER_OF) &&
                methodCodes.contains(APPROVE) &&
                containsAnyOfTransfer;
        return isErc721;
    }


    /**
     * Erc1155检验方法：
     * 如果合约有 supportsInterface 方法，校验 supportsInterface(0xd9b67a26) 返回值, 否则下一步
     */
//    public static boolean isErc1155(String contractAddress, Set<String> methodCodes) {
//        Function supportsInterface = FunctionConstant.supportsInterface(hexStringToByteArray("0xd9b67a26"));
//        if (methodCodes.contains(encode(supportsInterface))) {
//            Boolean result = (Boolean) Web3jUtil.quickCallReadMethod(supportsInterface, contractAddress, DefaultBlockParameterName.LATEST);
//            return Objects.nonNull(result) && result;
//        }
//        return false;
//
//    }
}
