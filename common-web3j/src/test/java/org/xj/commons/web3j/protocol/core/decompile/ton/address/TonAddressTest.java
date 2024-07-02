package org.xj.commons.web3j.protocol.core.decompile.ton.address;


import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;

import java.util.Base64;

class TonAddressTest {

    public static void test_cell_to_address(){
        Cell[] cells = Cell.fromHex("b5ee9c720101010100240000438017be7f50131a2536a969ee76b758ba3c3fb23d60a82d38722dbf9199aa6da20010");
        TonAddress tonAddress = cells[0].readAddress();
        System.out.println(tonAddress.toEAddress());
    }

    public static void test_address_from_hex(){
        TonAddress tonAddress = TonAddress.create("EQDfHLQlDtHJCyvyJ1B8TjXStxcH_zrBJVLHEIkV1HgBYqqG");
        byte[] bytes = Base64.getDecoder().decode("df1cb4250ed1c90b2bf227507c4e35d2b71707ff3ac12552c7108915d4780162");
        System.out.println( tonAddress.toEAddress());
        System.out.println( tonAddress.toHexFormat());
    }

    public static void main(String[] args) {
        test_address_from_hex();
    }
}