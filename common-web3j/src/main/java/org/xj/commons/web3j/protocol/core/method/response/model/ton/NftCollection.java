package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/8 14:09
 */
@Data
public class NftCollection {
    private String address;
    private BigInteger next_item_index;
    private Wallet owner;
    private String raw_collection_content;
    private CollectionMeta metadata;
    //previews[] 图片省略
    //approved_by[]
}
