package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/8 14:09
 */
@Data
public class NftItem {
    private String address;
    /**
     * nft的item_id
     */
    private BigInteger index;
    private Wallet owner;
    private Boolean verified;
    private Collection collection;
    //metadata:{} 空对象，省略
    //previews[] 图片省略
    //approved_by[]
}
