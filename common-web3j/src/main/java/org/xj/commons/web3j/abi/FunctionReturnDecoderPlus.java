package org.xj.commons.web3j.abi;

import org.xj.commons.web3j.abi.datatypes.RawData;
import org.xj.commons.toolkit.StringUtils;
import org.web3j.abi.DefaultFunctionReturnDecoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;

import java.util.Collections;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/9/29 14:12
 */
public class FunctionReturnDecoderPlus extends DefaultFunctionReturnDecoder {

    private static List<TypeReference<Type>> BYTE32_SINGLETON_LIST;

    @SuppressWarnings("unchecked")
    private static List<TypeReference<Type>> byte32SingletonList() {
        if (BYTE32_SINGLETON_LIST == null) {
            BYTE32_SINGLETON_LIST = (List) Collections.singletonList(new TypeReference<Bytes32>() {
            });
        }
        return BYTE32_SINGLETON_LIST;
    }

    public static List<Type> decode(String rawInput, List<TypeReference<Type>> outputParameters) {
        return FunctionReturnDecoder.decode(rawInput, outputParameters);
    }

    @Override
    public List<Type> decodeFunctionResult(String rawInput, List<TypeReference<Type>> outputParameters) {
        if (isSingleton(outputParameters) && RawData.class.equals(outputParameters.get(0).getType())) {
            return Collections.singletonList(new RawData(rawInput));
        }
        List<Type> result = super.decodeFunctionResult(rawInput, outputParameters);
        if (isSingleton(outputParameters)) {
            adaptByte32ToUtf8String(rawInput, outputParameters, result);
        }
        return result;
    }

    private static boolean isSingleton(List list) {
        return list != null && list.size() == 1;
    }

    private static void adaptByte32ToUtf8String(String rawInput, List<TypeReference<Type>> outputParameters, List<Type> result) {
        //如果rawInput是byte32特征，并且outputParameters是uft8String，并且result解析为空，尝试用byte32解析
        boolean adaptRule = isSingleton(result) && StringUtils.isEmpty(result.get(0).getValue()) && isByte32(rawInput) && isUtf8String(outputParameters.get(0));
        if (!adaptRule) {
            return;
        }
        //命中规则，尝试使用byte32解析
        try {
            List<Type> decode = FunctionReturnDecoder.decode(rawInput, byte32SingletonList());
            if (isSingleton(decode)) {
                String s = new String((byte[]) decode.get(0).getValue()).trim();
                result.set(0, new Utf8String(s));
            }
        } catch (Exception e) {
            //适配失败，保持原样
        }
    }

    private static boolean isUtf8String(TypeReference<Type> outputParameter) {
        try {
            return Utf8String.class.getName().equals(outputParameter.getType().getTypeName());
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isByte32(String rawInput) {
        return !StringUtils.isEmpty(rawInput) && rawInput.length() == 66 && rawInput.startsWith("0x");
    }
}
