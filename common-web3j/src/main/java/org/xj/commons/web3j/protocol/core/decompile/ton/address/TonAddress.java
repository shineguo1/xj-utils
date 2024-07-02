package org.xj.commons.web3j.protocol.core.decompile.ton.address;

import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.protocol.core.decompile.ton.util.Crc16;
import org.web3j.utils.Numeric;

import java.util.Base64;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/11 16:01
 */
public class TonAddress {

    private final static int BOUNCEABLE_TAG = 0x11;
    private final static int NON_BOUNCEABLE_TAG = 0x51;
    private final static int TEST_FLAG = 0x80;

    private Integer workChain;
    private byte[] hashPart;

    public TonAddress(int workChain, byte[] hashPart) {
        if (hashPart == null || hashPart.length != 32) {
            throw new IllegalArgumentException("illegal input parameter");
        }
        this.workChain = workChain;
        this.hashPart = hashPart;
    }

    public static TonAddress create(String input) {
        if (StringUtils.equalsIgnoreCase(input, "TON")) {
            //特殊约定，native币
            return nativeTon;
        } else {
            return new TonAddress(input);
        }
    }

    private TonAddress(String input) {
        try {
            // 1byte tag + 1byte workchain + 32 bytes hash + 2 byte crc
            if (input.contains(":")) {
                //hex
                String[] split = input.split(":");
                workChain = Integer.valueOf(split[0]);
                hashPart = Numeric.hexStringToByteArray(split[1]);
            } else {
                //base64
                String standardBase64 = input
                        .replaceAll("_", "/")
                        .replaceAll("-", "+");
                byte[] decode = Base64.getDecoder().decode(standardBase64);
                workChain = (int) decode[1];
                hashPart = new byte[32];
                System.arraycopy(decode, 2, hashPart, 0, 32);
            }
        } catch (Exception e) {
            throw new RuntimeException("can't decode address " + input);
        }
    }

    public static boolean isFriendly(String address) {
        return !address.contains(":");
    }

    public String toHexFormat() {
        return workChain + ":" + Numeric.toHexStringNoPrefix(hashPart);
    }

    public String toBase64Format(byte tag, boolean urlSafe) {
        byte[] bytes = new byte[36];
        bytes[0] = tag;
        bytes[1] = workChain.byteValue();
        System.arraycopy(hashPart, 0, bytes, 2, 32);
        byte[] crc16 = Crc16.crc16(bytes);
//        System.out.println(crc16[0]);
//        System.out.println(crc16[1]);
        System.arraycopy(crc16, 0, bytes, 34, 2);
        String base64 = Base64.getEncoder().encodeToString(bytes);
        if (urlSafe) {
            base64 = base64.replaceAll("/", "_")
                    .replaceAll("\\+", "-");
        }
        return base64;
    }

    public String toEAddress() {
        return toBase64Format((byte) BOUNCEABLE_TAG, true);
    }

    public String toUAddress() {
        return toBase64Format((byte) NON_BOUNCEABLE_TAG, true);
    }


    public static void main(String[] args) {
    /*
    测试样例：
    {
      "raw_form": "0:210f998d615ff337c9541ce1156ee8525f5f54ab7f402416626d83e9667183a3",
      "bounceable": {
        "b64": "EQAhD5mNYV/zN8lUHOEVbuhSX19Uq39AJBZibYPpZnGDoywr",
        "b64url": "EQAhD5mNYV_zN8lUHOEVbuhSX19Uq39AJBZibYPpZnGDoywr"
      },
      "non_bounceable": {
        "b64": "UQAhD5mNYV/zN8lUHOEVbuhSX19Uq39AJBZibYPpZnGDo3Hu",
        "b64url": "UQAhD5mNYV_zN8lUHOEVbuhSX19Uq39AJBZibYPpZnGDo3Hu"
      },
      "given_type": "friendly_bounceable",
      "test_only": false
    }
     */
//        TonAddress address = new TonAddress("EQAhD5mNYV_zN8lUHOEVbuhSX19Uq39AJBZibYPpZnGDoywr");
        TonAddress address = TonAddress.create("TON");
        System.out.println(address.toUAddress());
        System.out.println(address.toEAddress());
        System.out.println(address.toHexFormat());
    }

    public final static TonAddress nativeTon = new TonAddress(0, new byte[32]) {

        public String toHexFormat() {
            return "TON";
        }

        public String toBase64Format(byte tag, boolean urlSafe) {
            return "TON";
        }

        @Override
        public String toEAddress() {
            return "TON";
        }

        @Override
        public String toUAddress() {
            return "TON";
        }

    };


}
