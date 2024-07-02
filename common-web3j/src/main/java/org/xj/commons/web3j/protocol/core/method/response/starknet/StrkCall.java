package org.xj.commons.web3j.protocol.core.method.response.starknet;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.web3j.protocol.core.Response;
import org.web3j.utils.EnsUtils;

import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/12 9:54
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StrkCall extends Response<List<String>> {


    public List<String> getValue() {
        return getResult();
    }

    public boolean isReverted() {
        if (hasError() && getError().getCode() == 3 && getError().getData() != null) {
            return !EnsUtils.isEIP3668(getError().getData());
        }

        return hasError() || isErrorInResult();
    }

    @Deprecated
    public boolean reverts() {
        return isReverted();
    }

    private boolean isErrorInResult() {
        return false;
    }

    public String getRevertReason() {
        if (isErrorInResult()) {
            return "ErrorInResult not decoded";
        } else if (hasError()) {
            return getError().getMessage();
        }
        return null;
    }
}
