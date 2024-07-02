package org.xj.commons.web3j.event.model;

import org.xj.commons.web3j.event.core.EventDecoder;
import org.xj.commons.web3j.event.core.EventField;
import org.xj.commons.web3j.event.core.EventName;
import lombok.Data;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/7/1 17:18
 */
@Data
@EventName("UnlockReward")
public class UnlockReward {
    //address indexed user, uint256 indexed pid, address to, uint256 amount, uint256 unlockTime

    @EventField(indexed = true)
    private Address user;
    @EventField(indexed = true)
    Uint256 pid;
    @EventField(indexed = false)
    Address to;
    @EventField(indexed = false)
    Uint256 amount;
    @EventField(indexed = false)
    Uint256 unlockTime;

    public static void main(String[] args) {
        System.out.println(EventDecoder.create(UnlockReward.class).getSignature());
    }
}
