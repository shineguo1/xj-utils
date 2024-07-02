package org.xj.commons.web3j.sideoutput;

import lombok.Getter;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/12 10:56
 */
@Getter
public abstract class OutputTag<T> {

    protected String tag;
    protected Class<T> valueClass;


    protected String getInternalKey() {
        return tag + "@" + valueClass.getName();
    }

}
