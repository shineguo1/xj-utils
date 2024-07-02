package org.xj.commons.web3j.req;

import org.xj.commons.web3j.abi.HexDecoder;
import org.xj.commons.web3j.protocol.core.JsonRpc_Tron_Web3j;
import org.xj.commons.web3j.protocol.core.decompile.tron.TronAddressUtils;
import org.xj.commons.web3j.protocol.core.method.response.TronGetAssetIssue;
import org.xj.commons.web3j.protocol.core.method.response.model.TronAssetIssue;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;

/**
 * @author xj
 * @Date 2023/11/2 14:17
 */
@Slf4j
public class TronGetAssetIssueByIdReq implements Web3Cmd<TronGetAssetIssue, TronAssetIssue> {

    private Long tokenId;

    public TronGetAssetIssueByIdReq(Long tokenId) {
        this.tokenId = tokenId;
    }

    @Override
    public Request<?, TronGetAssetIssue> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Tron_Web3j) {
            return ((JsonRpc_Tron_Web3j) web3j).tronGetAssetIssueById(tokenId);
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TronGetAssetIssueByIdReq");
        }
    }

    @Override
    public TronAssetIssue getResult(TronGetAssetIssue response) {
        TronAssetIssue result = response.getResult();
        if (result != null) {
            try {
                //把41前缀改为0x前缀
                result.setOwnerAddress(result.getOwnerAddress());
                result.setAbbr(TronAddressUtils.formatEvmAddress(result.getAbbr()));
                result.setDescription(HexDecoder.decodeHexAsString(result.getDescription()));
                result.setName(HexDecoder.decodeHexAsString(result.getName()));
                result.setUrl(HexDecoder.decodeHexAsString(result.getUrl()));
            } catch (Exception e) {
                log.error("getAssetIssueById 返回解码失败，error:{}", e.getMessage());
                return null;
            }
        }
        return result;
    }

}
