package org.xj.commons.web3j.sideoutput;

import lombok.Getter;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/12 10:10
 */
@Getter
public class ValueOutputTag<T> extends OutputTag<T> {

    public ValueOutputTag(String tag, Class<T> valueClass) {
        this.tag = tag;
        this.valueClass = valueClass;
    }
}
