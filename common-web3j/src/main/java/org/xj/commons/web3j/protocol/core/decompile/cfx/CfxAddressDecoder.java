package org.xj.commons.web3j.protocol.core.decompile.cfx;

import com.google.common.collect.Lists;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CfxAddressDecoder {
    static final Pattern pattern = Pattern.compile("^([^:]+):(.+:)?(.{34})(.{8})$");

    private static final String TYPE_USER = "user";
    private static final String TYPE_CONTRACT = "contract";
    private static final String TYPE_BUILTIN = "builtin";
    private static final String TYPE_NULL = "null";
    private static final String TYPE_UNKNOWN = "unknown";

    private static final String PREFIX_CFX = "cfx";
    private static final String PREFIX_CFXTEST = "cfxtest";
    private static final String PREFIX_NET = "net";

    private static final int NETID_MAIN = 1029;
    private static final int NETID_TEST = 1;
    private static final long NET_ID_LIMIT = 0xFFFFFFFFL;
    private static final String ZERO_ADDRESS_HEX = "0x0000000000000000000000000000000000000000";
    private static final String ADMIN_CONTROL = "0x0888000000000000000000000000000000000000";
    private static final String SPONSOR_CONTROL = "0x0888000000000000000000000000000000000001";
    private static final String STAKING = "0x0888000000000000000000000000000000000002";
    private static final String CONFLUX_CONTEXT = "0x0888000000000000000000000000000000000004";
    private static final String POS_REGISTER = "0x0888000000000000000000000000000000000005";
    private static final String CROSS_SPACE_CALL = "0x0888000000000000000000000000000000000006";
    private static final String PARAMS_CONTROL = "0x0888000000000000000000000000000000000007";


    private static final String ALPHABET = "ABCDEFGHJKMNPRSTUVWXYZ0123456789";


    private static final BigInteger BIGINT_0 = BigInteger.ZERO;
    private static final BigInteger BIGINT_1 = BigInteger.ONE;
    private static final BigInteger BIGINT_5 = BigInteger.valueOf(5);
    private static final BigInteger BIGINT_35 = BigInteger.valueOf(35);
    private static final BigInteger BIGINT_0B00001 = BigInteger.valueOf(0b00001);
    private static BigInteger BIGINT_0B00010 = BigInteger.valueOf(0b00010);
    private static final BigInteger BIGINT_0B00100 = BigInteger.valueOf(0b00100);
    private static final BigInteger BIGINT_0B01000 = BigInteger.valueOf(0b01000);
    private static final BigInteger BIGINT_0B10000 = BigInteger.valueOf(0b10000);
    private static final BigInteger BIGINT_0X07FFFFFFFF = BigInteger.valueOf(0x07ffffffffL);
    private static final BigInteger BIGINT_0X98F2BC8E61 = BigInteger.valueOf(0x98f2bc8e61L);
    private static final BigInteger BIGINT_0X79B76D99E2 = BigInteger.valueOf(0x79b76d99e2L);
    private static final BigInteger BIGINT_0XF33E5FB3C4 = BigInteger.valueOf(0xf33e5fb3c4L);
    private static final BigInteger BIGINT_0XAE2EABE2A8 = BigInteger.valueOf(0xae2eabe2a8L);
    private static final BigInteger BIGINT_0X1E4F43E470 = BigInteger.valueOf(0x1e4f43e470L);

    static Map<Character, Integer> ALPHABET_MAP = new HashMap<>();

    static {
        for (int z = 0; z < ALPHABET.length(); z++) {
            char x = ALPHABET.charAt(z);
            if (ALPHABET_MAP.containsKey(x)) {
                throw new IllegalArgumentException(x + " is ambiguous");
            }
            ALPHABET_MAP.put(x, z);
        }
    }

    @SuppressWarnings({"AlibabaRemoveCommentedCode"})
    private static CfxAddress decode(String address) throws Exception {
        if (address == null || address.isEmpty()) {
            throw new IllegalArgumentException("Empty address");
        }
        Matcher matcher = pattern.matcher(address.toUpperCase());
        if (!matcher.find()) {
            throw new RuntimeException("address is invalid");
        }
        String netName = matcher.group(1);
        String shouldHaveType = matcher.group(2);
        String payload = matcher.group(3);
        String checksum = matcher.group(4);

        /*
        不做校验
        byte[] prefix5Bytes = Buffer.from(netName);
        int[] prefix5Bits = new int[prefix5Bytes.length];
        for (int i = 0; i < prefix5Bytes.length; i++) {
            byte b = prefix5Bytes[i];
            prefix5Bits[i] = b & 0b11111;
        }
        */

        List<Integer> payload5Bits = Lists.newArrayList();
        for (int i = 0; i < payload.toCharArray().length; i++) {
            char c = payload.charAt(i);
            payload5Bits.add(ALPHABET_MAP.get(c));
        }
        List<Integer> bits = convertBit(payload5Bits, 5, 8, false);
        int netId = decodeNetId(netName);
        int[] hexAddress = new int[bits.size() - 1];
        for (int i = 1; i < bits.size(); i++) {
            hexAddress[i - 1] = bits.get(i);
        }
        String addressType = getAddressType(hexAddress);
        System.out.println(bits);
        System.out.println(netId);
        System.out.println(addressType);
        /*
        不做校验
        if (shouldHaveType && `type.${type}:` !== shouldHaveType.toLowerCase()) {
            throw new Error('Type of address doesn\'t match')
        }
        const bigInt = polyMod([...prefix5Bits, 0, ...payload5Bits, ...checksum5Bits])
        if (Number(bigInt)) {
            throw new Error(`Invalid checksum for ${address}`)
        }
        */
        String evmAddress = toHexString(hexAddress).toLowerCase();
        int version = bits.get(0);
        CfxAddress ret = new CfxAddress();
        ret.setCfxAddress(address);
        ret.setEvmAddress(evmAddress);
        ret.setType(addressType);
        ret.setHexValue(hexAddress);
        ret.setVersion(version);
        return ret;
    }

    private static int decodeNetId(String payload) throws Exception {
        String payloadLowerCase = payload.toLowerCase();
        switch (payloadLowerCase) {
            case PREFIX_CFXTEST:
                return NETID_TEST;
            case PREFIX_CFX:
                return NETID_MAIN;
            default: {
                String prefix = payloadLowerCase.substring(0, 3);
                String netId = payloadLowerCase.substring(3);
                if (!PREFIX_NET.equals(prefix) || !isValidNetId(netId)) {
                    throw new Exception("netId prefix should be passed by 'cfx', 'cfxtest' or 'net[n]'");
                }
                int numberNetId = Integer.parseInt(netId);
                if (numberNetId == NETID_TEST || numberNetId == NETID_MAIN) {
                    throw new Exception("net1 or net1029 are invalid");
                }
                return numberNetId;
            }
        }
    }

    private static boolean isValidNetId(String netId) {
        try {
            long id = Long.parseLong(netId);
            return id < NET_ID_LIMIT;
        } catch (Exception e) {
            return false;
        }
    }

    public static String getAddressType(int[] hexAddress) {
        if (hexAddress.length < 1) {
            throw new RuntimeException("Empty payload in address");
        }

        switch (hexAddress[0] & 0xf0) {
            case 0x10:
                return TYPE_USER;
            case 0x80:
                return TYPE_CONTRACT;
            case 0x00:
                for (int x : hexAddress) {
                    if (x != 0x00) {
                        return TYPE_BUILTIN;
                    }
                }
                return TYPE_NULL;
            default:
                return TYPE_UNKNOWN;
            // throw new Error('hexAddress should start with 0x0, 0x1 or 0x8')
        }
    }

    public static List<Integer> convertBit(List<Integer> buffer, int inBits, int outBits, boolean pad) {
        int mask = (1 << outBits) - 1;
        List<Integer> result = new ArrayList<>();
        int bits = 0;
        int value = 0;

        for (int i = 0; i < buffer.size(); i++) {
            bits += inBits;
            value = (value << inBits) | buffer.get(i);

            while (bits >= outBits) {
                bits -= outBits;
                result.add((value >>> bits) & mask);
            }
        }

        value = (value << (outBits - bits)) & mask;
        if (pad && bits > 0) {
            result.add(value);
        } else if (value != 0 && !pad) {
            throw new IllegalArgumentException("Excess padding");
        } else if (bits >= inBits && !pad) {
            throw new IllegalArgumentException("Non-zero padding");
        }

        return result;
    }

    public static BigInteger polyMod(BigInteger[] buffer) {
        BigInteger checksumBigInt = BigInteger.ONE;

        for (BigInteger byteInt : buffer) {
            BigInteger high = checksumBigInt.shiftRight(BIGINT_35.intValue());

            checksumBigInt = checksumBigInt.and(BIGINT_0X07FFFFFFFF);
            checksumBigInt = checksumBigInt.shiftLeft(BIGINT_5.intValue());

            checksumBigInt = byteInt.equals(BigInteger.ZERO) ? checksumBigInt : checksumBigInt.xor(byteInt);

            if (!high.and(BIGINT_0B00001).equals(BigInteger.ZERO)) {
                checksumBigInt = checksumBigInt.xor(BIGINT_0X98F2BC8E61);
            }
            if (!high.and(BIGINT_0B00010).equals(BigInteger.ZERO)) {
                checksumBigInt = checksumBigInt.xor(BIGINT_0X79B76D99E2);
            }
            if (!high.and(BIGINT_0B00100).equals(BigInteger.ZERO)) {
                checksumBigInt = checksumBigInt.xor(BIGINT_0XF33E5FB3C4);
            }
            if (!high.and(BIGINT_0B01000).equals(BigInteger.ZERO)) {
                checksumBigInt = checksumBigInt.xor(BIGINT_0XAE2EABE2A8);
            }
            if (!high.and(BIGINT_0B10000).equals(BigInteger.ZERO)) {
                checksumBigInt = checksumBigInt.xor(BIGINT_0X1E4F43E470);
            }
        }

        return checksumBigInt.xor(BigInteger.ONE);
    }

    private static String toHexString(int[] hexAddress) {
        StringBuilder sb = new StringBuilder("0x");
        for (int address : hexAddress) {
            sb.append(String.format("%02X", address));
        }
        return sb.toString();
    }


    public static void main(String[] args) throws Exception {
        CfxAddress decode = decode("cfx:aamjy3abae3j0ud8ys0npt38ggnunk5r4ps2pg8vcc");
        System.out.println(decode);
    }
}