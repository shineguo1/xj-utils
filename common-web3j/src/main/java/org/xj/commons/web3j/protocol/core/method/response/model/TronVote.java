package org.xj.commons.web3j.protocol.core.method.response.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

/**
 * @author xj
 * @Date 2023/10/23
 */
@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class TronVote {

    private String voteAddress;

    private Long voteCount;

}

