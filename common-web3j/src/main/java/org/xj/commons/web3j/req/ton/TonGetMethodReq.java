package org.xj.commons.web3j.req.ton;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import org.xj.commons.web3j.abi.datatypes.ton.TonFunction;
import org.xj.commons.web3j.protocol.core.JsonRpc_Ton_Web3j;
import org.xj.commons.web3j.protocol.core.decompile.ton.util.StackDecoder;
import org.xj.commons.web3j.protocol.core.method.response.model.ton.TonMethodRes;
import org.xj.commons.web3j.protocol.core.method.response.ton.TonConsoleGetMethods;
import org.xj.commons.web3j.req.BatchReq;
import org.xj.commons.web3j.req.Web3Cmd;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.Request;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 10:26
 */
public class TonGetMethodReq<RES> implements Web3Cmd<TonConsoleGetMethods, RES>, BatchReq {

    private final String address;
    private final TonFunction<RES> function;
    private TypeReference<RES> responseTypeReference;
    private boolean decodeByStack = false;

    public TonGetMethodReq(String account, TonFunction<RES> function) {
        this.address = account;
        this.function = function;
        this.responseTypeReference = function.getOutputParams();
    }

    public TonGetMethodReq<RES> decodeByStack(boolean b) {
        this.decodeByStack = b;
        return this;
    }


    @Override
    public Request<?, TonConsoleGetMethods> getRequest(Web3j web3j, DefaultBlockParameter defaultBlock) {
        if (web3j instanceof JsonRpc_Ton_Web3j) {
            return ((JsonRpc_Ton_Web3j) web3j).tonGetAccountMethod(address, function.getName(), function.getInputParams());
        } else {
            throw new UnsupportedOperationException(web3j.getClass().getSimpleName() + "不支持 TonGetAccountReq");
        }
    }

    @Override
    public RES getResult(TonConsoleGetMethods response) {
        TonMethodRes<JSONObject> result = response.getResult();
        if (!result.isSuccess()) {
            //请求失败，返回null
            return null;
        }
        //请求成功，解码
        RES decode;
        if (decodeByStack) {
            //用`stack`解码
            decode = StackDecoder.decode(result.getStack(), responseTypeReference);
        } else {
            //直接使用`decoded`
            JSONObject decodeJsonObj = result.getDecoded();
            if (JSONObject.class.isAssignableFrom(responseTypeReference.getRawType())) {
                decode = (RES) result.getDecoded();
            } else {
                decode = decodeJsonObj.to(responseTypeReference);
            }
        }
        return decode;
    }

}
