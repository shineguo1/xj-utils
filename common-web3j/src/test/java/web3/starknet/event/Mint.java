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
@EventName(value = "Mint", chainType = "STRK")
public class Mint {
    //contact: 0x07c2e1e733f28daa23e78be3a4f6c724c0ab06af65f6a95b5e0545215f1abc1b

    @EventField
    private Address252 to;
    @EventField
    private Uint256 amount;

}
