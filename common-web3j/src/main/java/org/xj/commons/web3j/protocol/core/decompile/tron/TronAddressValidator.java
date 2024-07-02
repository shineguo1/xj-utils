package org.xj.commons.web3j.protocol.core.decompile.tron;

import org.xj.commons.toolkit.ArrayUtils;
import org.xj.commons.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * @author xj
 * @date on 2023/10/19 14:43
 */
@Slf4j
public class TronAddressValidator {
    private static byte ADD_PRE_FIX_BYTE_MAINNET = (byte) 0x41;   //41 + address
    private static int ADDRESS_SIZE = 21;


    public static boolean addressValid(String addr) {
        return addressValid(decodeFromBase58Check(addr));
    }

    public static boolean addressValid(byte[] address) {
        if (ArrayUtils.isEmpty(address)) {
            return false;
        }
        if (address.length != ADDRESS_SIZE) {
            return false;
        }
        byte preFixbyte = address[0];
        if (preFixbyte != ADD_PRE_FIX_BYTE_MAINNET) {
            return false;
        }
        //Other rule;
        return true;
    }


    public static String encode58Check(byte[] input) {
        byte[] hash0 = Sha256Hash.hash(input);
        byte[] hash1 = Sha256Hash.hash(hash0);
        byte[] inputCheck = new byte[input.length + 4];
        System.arraycopy(input, 0, inputCheck, 0, input.length);
        System.arraycopy(hash1, 0, inputCheck, input.length, 4);
        return Base58.encode(inputCheck);
    }

    private static byte[] decode58Check(String input) {
        byte[] decodeCheck = Base58.decode(input);
        if (decodeCheck.length <= 4) {
            return null;
        }
        byte[] decodeData = new byte[decodeCheck.length - 4];
        System.arraycopy(decodeCheck, 0, decodeData, 0, decodeData.length);
        byte[] hash0 = Sha256Hash.hash(decodeData);
        byte[] hash1 = Sha256Hash.hash(hash0);
        if (hash1[0] == decodeCheck[decodeData.length] &&
                hash1[1] == decodeCheck[decodeData.length + 1] &&
                hash1[2] == decodeCheck[decodeData.length + 2] &&
                hash1[3] == decodeCheck[decodeData.length + 3]) {
            return decodeData;
        }
        return null;
    }

    public static byte[] decodeFromBase58Check(String addressBase58) {
        if (StringUtils.isEmpty(addressBase58)) {
            return null;
        }
        byte[] address = decode58Check(addressBase58);
        if (!addressValid(address)) {
            return null;
        }
        return address;
    }

    public static boolean priKeyValid(byte[] priKey) {
        if (ArrayUtils.isEmpty(priKey)) {
            return false;
        }
        if (priKey.length != 32) {
            return false;
        }
        //Other rule;
        return true;
    }

    public static void main(String[] args) {
        System.out.println(addressValid("TF9i9VEzaayhog5EmGuq4hhYZnnDtodta3"));
        System.out.println(addressValid("TF9i9vEzaayhog5EmGuq4hhYZnnDtodta3"));
        System.out.println(addressValid("TF9i9vEzaayhog5EmGuq4hhYZnndtodta3"));
        System.out.println(addressValid("T9zTTVfegCiJ5ovip4y2dCPiEdXT9EmtEw"));
        System.out.println(addressValid("T9zTTVfegCij5ovip4y2dCPiEdXT9EmtEw"));
        System.out.println(addressValid("T9ztTVfegCij5ovip4y2dCPiEdXT9EmtEw"));
    }

}
