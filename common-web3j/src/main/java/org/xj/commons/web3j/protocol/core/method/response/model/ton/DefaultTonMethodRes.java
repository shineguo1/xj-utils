package org.xj.commons.web3j.protocol.core.method.response.model.ton;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 11:14
 */
@Data
@Accessors(chain = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class DefaultTonMethodRes extends TonMethodRes<JSONObject> {
    private boolean success;
    private int exitCode;
    private List<TonType> stack;
    private JSONObject decode;
}
