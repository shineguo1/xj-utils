package org.xj.commons.web3j.protocol.core.decompile;

import org.xj.commons.toolkit.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/6/9 17:24
 */
public class Decompiler {


    private static List<Integer> hexToIntArray(String hexString) {
        if (hexString.startsWith("0x")) {
            hexString = StringUtils.substring(hexString, 2);
        }
        List<Integer> integers = Lists.newArrayList();
        for (int i = 0; i < hexString.length(); i += 2) {
            integers.add(Integer.parseInt(StringUtils.substring(hexString, i, i + 2), 16));
        }
        return integers;
    }


    /**
     * 十进制串转16进制串（每个数补足2位）
     */
    private static String hexConvert(List<Integer> ints) {
        String ret = "0x";
        for (Integer integer : ints) {
            if (integer != 0) {
                ret += (integer <= 0xf ? "0" : "") + Integer.toHexString(integer);
            } else {
                ret += "00";
            }
        }
        return ret;
    }

    private static Op opCodes(int op) {
        return CodesMap.get(op);
    }

    public static Set<String> parseMethodCode(String bytesRaw) {
        List<Integer> raw = hexToIntArray(bytesRaw);
        Set<String> methodCodes = Sets.newHashSet();
        for (int i = 0; i < raw.size(); i++) {
            Op op = opCodes(raw.get(i));
            if (StringUtils.startsWith(op.getName(), "PUSH")) {
                int length = raw.get(i) - 0x5f;
                //取后length位，不超过raw长度
                List<Integer> pushData = Lists.newArrayList();
                for (int j = i + 1; j < i + length + 1 && j < raw.size(); j++) {
                    pushData.add(raw.get(j));
                }
                //位数不够，用0补齐长度
                for (int j = pushData.size(); j < length; j++) {
                    pushData.add(0);
                }
                i += length;

                //method的长度是4，即PUSH4
                //TODO 遇到一个erc1155的method balanceOf(address,uint256)， 0x00fdd58e, 只找到push3 fdd58e, 遇到0x00前缀是否需要push3？
                if (length == 4) {
                    methodCodes.add(hexConvert(pushData));
                }
            }
        }
        return methodCodes;
    }
}
