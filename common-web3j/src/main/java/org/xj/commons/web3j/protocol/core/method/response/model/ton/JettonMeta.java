package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import lombok.Data;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/12/15 16:48
 */
@Data
public class JettonMeta {

    private String address;
    private String name;
    private String symbol;
    private Integer decimals;
    private String image;
    private String description;
}
