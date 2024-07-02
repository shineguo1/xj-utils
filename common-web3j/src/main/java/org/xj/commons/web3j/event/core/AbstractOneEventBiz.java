package org.xj.commons.web3j.event.core;

import org.xj.commons.toolkit.CollectionUtils;
import org.xj.commons.web3j.event.model.ContractParseContext;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/19 10:35
 */
public abstract class AbstractOneEventBiz<R, Context extends ContractParseContext<?,R>> extends BaseEventBiz<R, Context> {

    @Override
    public void parse(List<Context> decodeLogContext) {
        if (CollectionUtils.isNotEmpty(decodeLogContext)) {
            decodeLogContext.forEach(this::parse);
        }
    }
}
