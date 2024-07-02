package org.xj.commons.web3j.protocol.core.method.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;

import java.util.Collection;
import java.util.List;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/13 14:42
 */
@Getter
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class StrkFilter {

    /**
     * 1. String类型 - from_block : e.g. latest, pending, ...
     * 2. number类型 - from_block : { "block_number" : 8232347 }
     */
    private Object fromBlock;

    /**
     * @see #fromBlock
     */
    private Object toBlock;

    /**
     * 只能监听一个地址
     */
    private String address;

    private List<List<String>> keys;

    /**
     * 默认1000，最大10000
     */
    private int chunkSize = 1000;

    public StrkFilter() {

    }

    public StrkFilter(int chunkSize) {
        this.chunkSize = Math.min(chunkSize, 10000);
    }

    public StrkFilter setAddress(String address) {
        this.address = address;
        return this;
    }

    public StrkFilter setSingleTopic(String topic) {
        checkAndInitKeys();
        keys.get(0).clear();
        keys.get(0).add(topic);
        return this;
    }

    public StrkFilter addOptionalTopics(Collection<String> optionalTopics) {
        checkAndInitKeys();
        for (String optionalTopic : optionalTopics) {
            keys.get(0).add(optionalTopic);
        }
        return this;
    }


    public StrkFilter addOptionalTopics(String... optionalTopics) {
        checkAndInitKeys();
        for (String optionalTopic : optionalTopics) {
            keys.get(0).add(optionalTopic);
        }
        return this;
    }

    public StrkFilter setFromBlock(DefaultBlockParameter block) {
        this.fromBlock = getBlock(block);
        return this;
    }

    public StrkFilter setToBlock(DefaultBlockParameter block) {
        this.toBlock = getBlock(block);
        return this;
    }


    /**
     * @see #fromBlock
     */
    private Object getBlock(DefaultBlockParameter block) {
        Object blockValue;
        if (block instanceof DefaultBlockParameterName) {
            blockValue = block.getValue();
        } else if (block instanceof DefaultBlockParameterNumber) {
            blockValue = ImmutableMap.of("block_number", ((DefaultBlockParameterNumber) block).getBlockNumber());
        } else {
            throw new UnsupportedOperationException("Unsupported class: " + block.getClass().getSimpleName());
        }
        return blockValue;
    }


    private void checkAndInitKeys() {
        if (keys == null || keys.size() == 0) {
            keys = Lists.newArrayList();
            keys.add(Lists.newArrayList());
        }
    }
}
