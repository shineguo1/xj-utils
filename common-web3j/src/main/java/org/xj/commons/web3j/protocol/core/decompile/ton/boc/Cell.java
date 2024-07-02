package org.xj.commons.web3j.protocol.core.decompile.ton.boc;

import org.xj.commons.web3j.protocol.core.decompile.ton.address.TonAddress;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.util.Serialization;
import org.xj.commons.web3j.protocol.core.decompile.ton.util.PaddedBits;
import lombok.Getter;
import org.web3j.utils.Numeric;

import java.util.Base64;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/12 9:54
 */
@Getter
public class Cell {
    BitString bits;
    Cell[] refs;
    boolean isExotic = false;

    public Cell(BitString bits, Cell[] refs, boolean isExotic) {
        this.bits = bits;
        this.refs = refs;
        this.isExotic = isExotic;
    }

    public String b64() {
        return Base64.getEncoder().encodeToString(PaddedBits.bitsToBuffer(bits));
    }

    //Method
    //====================================

    public static Cell[] fromBoc(byte[] src) {
        return Serialization.deserializeBoc(src);
    }

    public static Cell[] fromHex(String src) {
        return fromBoc(Numeric.hexStringToByteArray(src));
    }

    public static Cell[] fromBase64(String src) {
        return fromBoc(Base64.getDecoder().decode(src));
    }

    public TonAddress readAddress() {
        return new BitReader(bits).loadMaybeAddress();
    }


}
