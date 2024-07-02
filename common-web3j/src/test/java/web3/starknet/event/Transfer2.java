package web3.starknet.event;

import org.xj.commons.web3j.abi.datatypes.Address252;
import org.xj.commons.web3j.event.core.EventField;
import org.xj.commons.web3j.event.core.EventName;
import lombok.Data;
import org.web3j.abi.datatypes.generated.Uint256;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 9:56
 */
@Data
@EventName(value = "Transfer", chainType = "STRK")
public class Transfer2 {
    //contact: 0x049d36570d4e46f48e99674bd3fcc84644ddd6b96f7c741b1562b82f9e004dc7
    @EventField
    private Address252 from;
    @EventField
    private Address252 to;
    @EventField
    private Uint256 value;
}
