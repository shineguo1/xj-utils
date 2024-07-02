package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import org.xj.commons.toolkit.BigIntegerUtils;
import org.xj.commons.toolkit.StringUtils;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;
import com.google.common.collect.Lists;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 11:16
 */
@Data
public class TonType {

    private String type;
    private String num;
    private String cell;
    private List<TonType> tuple;

    public BigInteger getValueAsNum() {
        if (isNum()) {
            return BigIntegerUtils.decode(num);
        } else {
            return null;
        }
    }

    public Cell[] getValueAsCell() {
        if (isCell()) {
            return Cell.fromHex(cell);
        } else {
            return null;
        }
    }


    public List<Object> getValueAsTuple() {
        if (isTuple()) {
            List<Object> ret = Lists.newArrayList();
            for (TonType tonType : tuple) {
                ret.add(tonType.getValue());
            }
            return ret;
        } else {
            return null;
        }
    }


    public Object getValue() {
        switch (type) {
            case "num":
                return getValueAsNum();
            case "cell":
                return getValueAsCell();
            case "tuple":
                return getValueAsTuple();
            default:
                return null;
        }
    }

    public boolean isNum(){
        return StringUtils.equalsIgnoreCase(type, "num");
    }

    public boolean isCell() {
        return StringUtils.equalsIgnoreCase(type, "cell");
    }

    public boolean isTuple() {
        return StringUtils.equalsIgnoreCase(type, "tuple");
    }
}
