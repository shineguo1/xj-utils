package org.xj.commons.web3j.abi;

import org.web3j.abi.TypeDecoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.utils.Numeric;

import java.nio.charset.StandardCharsets;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/11/27 10:16
 */
public class HexDecoder {


    public static String decodeHexAsString(String input){
        return new String(Numeric.hexStringToByteArray(input), StandardCharsets.UTF_8);
    }

    public static Address decodeHexAsAddress(String input){
        String s = input.length() >=2 ? input.substring(2) : "";
        return TypeDecoder.decode(String.format("%64s", s).replace(' ', '0')
                , Address.class);
    }
}
