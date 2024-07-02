package org.xj.commons.web3j.sideoutput;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/12 10:56
 */
public class MapOutputTag<T> extends OutputTag<T> {

    public MapOutputTag(String tag, Class<T> valueClass) {
        this.tag = tag;
        this.valueClass = valueClass;
    }

}
