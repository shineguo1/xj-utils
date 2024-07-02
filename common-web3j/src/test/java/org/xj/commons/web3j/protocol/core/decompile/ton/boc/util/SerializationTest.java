package org.xj.commons.web3j.protocol.core.decompile.ton.boc.util;


import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Boc;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;
import org.web3j.utils.Numeric;

import java.util.Base64;

class SerializationTest {



    public static void main(String[] args) {
        test_cell();
    }

    public static void test_cell(){
        /*
         * curl --request POST \
         *   --url https://toncenter.com/api/v2/runGetMethod \
         *   --header 'content-type: application/json' \
         *   --data '{
         *   "address": "EQCajaUU1XXSAjTD-xOV7pE49fGtg4q8kF3ELCOJtGvQFQ2C",
         *   "method": "get_jetton_data",
         *   "stack": [
         *   ]
         * }'
         */

        byte[] bytes = Base64.getDecoder().decode("te6cckECDwEAARkAAQMAwAECASACAwFDv/CC62Y7V6ABkvSmrEZyiN8t/t252hvuKPZSHIvr0h8ewAQCASAFBgA6AGh0dHBzOi8vd3Rvbi5kZXYvbG9nbzE5Mi5wbmcCASAHCAIBIAsMAUG/RUam/+G3nP3Ya609uHQxPc3i+wXmp0qn81UtlhfHnRMJAUG/btT5QqeEjOLLBmt3oRKMah/4xD9Dii3OJGErqf+riwMKABgAV3JhcHBlZCBUT04ACgBXVE9OAUG/Ugje9G9aHU+dzmarMJ9KhRMF8Wb5Hvedkj71jjT5ogkNAUG/XQH6XjwGkBxFBGxrLdzqWvdk/qDu1yoQ1ATyMSzrJH0OACIAV3JhcHBlZCBUT04gQ29pbgAEADkKN6Cv");
        Cell[] cells = Serialization.deserializeBoc(bytes);
    }

    public static void test_boc(){
        byte[] bytes = Numeric.hexStringToByteArray("b5ee9c72010101010024000043800ef3b9902a271b2a01c8938a523cfe24e71847aaeb6a620001ed44a77ac0e709d0");
        Boc boc = Serialization.parseBoc(bytes);
    }
}