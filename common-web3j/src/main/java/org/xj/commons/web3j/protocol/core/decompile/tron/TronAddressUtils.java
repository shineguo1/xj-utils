package org.xj.commons.web3j.protocol.core.decompile.tron;

import org.xj.commons.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.util.Objects;

/**
 * @author xj
 * @Date 2023/10/23
 */
@Slf4j
public class TronAddressUtils {

    public static String getAddress(String tronAddress) {
        byte[] bytes = TronWalletUtils.decodeFromBase58Check(tronAddress);
        BigInteger bigInteger = new BigInteger(1, bytes);
        String address = bigInteger.toString(16).substring(2);
        return "0x"+address;
    }

    public static String getEvmAddress(String tronAddress){
        return getAddress(tronAddress);
    }

    public static String formatEvmAddress(String address){
        int length = StringUtils.length(address);
        if(length == 42){
            return "0x" + address.substring(2);
        } else if (length == 40) {
            return "0x" + address;
        }else {
            return address;
        }
    }

    public static String getTronAddress(String address) {
        try {
            if (Objects.isNull(address)) {
                return null;
            }
            String substring = address.substring(2);
            //41转成0x
            byte[] bytes1 = Numeric.hexStringToByteArray("41" + substring);
            return TronWalletUtils.encode58Check(bytes1);
        } catch (Exception e) {
            log.error("address convert tronAddress is error:{}-{}",address,e);
            return null;
        }
    }

    public static void main(String[] args) {
        System.out.println(getTronAddress("0xf0cc5a2a84cd0f68ed1667070934542d673acbd8"));
        System.out.println(getAddress("TPyjyZfsYaXStgz2NmAraF1uZcMtkgNan5"));
    }


}
