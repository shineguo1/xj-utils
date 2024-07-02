package org.xj.commons.web3j.protocol.core.decompile.ton.boc;


class BitStringTest {

    public static void main(String[] args) {
        showBit();
    }

    private static void showBit() {
        BitString bitString = new BitString(new byte[]{0x0f, (byte) 0xd9, (byte) 0xd6}, 5, 10);
        for (int i = 0; i < bitString.length(); i++) {
            System.out.println(i + " " + bitString.at(i));
        }
    }
}