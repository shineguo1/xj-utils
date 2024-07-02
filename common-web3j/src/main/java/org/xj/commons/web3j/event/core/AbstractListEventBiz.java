package org.xj.commons.web3j.event.core;


import org.xj.commons.web3j.event.model.ContractParseContext;

import java.util.Collections;
import java.util.Objects;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/12/19 10:35
 */
public abstract class AbstractListEventBiz<R, Context extends ContractParseContext<?, R>> extends BaseEventBiz<R, Context> {

    @Override
    public void parse(Context decodeLogContext) {
        if (Objects.nonNull(decodeLogContext)) {
            this.parse(Collections.singletonList(decodeLogContext));
        }
    }
}
