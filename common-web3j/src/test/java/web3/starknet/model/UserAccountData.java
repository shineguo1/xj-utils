package web3.starknet.model;

import lombok.Data;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.generated.Uint256;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/3/14 11:22
 */
@Data
public class UserAccountData extends StaticStruct {

    public BigInteger adjTotalCollateral;
    public BigInteger adjTotalDebt;
    public Boolean isValidDebt;
    public BigInteger healthFactor;

    public UserAccountData(
            Uint256 adjTotalCollateral,
            Uint256 adjTotalDebt,
            Bool isValidDebt,
            Uint256 healthFactor) {
        super(adjTotalCollateral, adjTotalDebt, isValidDebt, healthFactor);
        this.adjTotalCollateral = adjTotalCollateral.getValue();
        this.adjTotalDebt = adjTotalDebt.getValue();
        this.isValidDebt = isValidDebt.getValue();
        this.healthFactor = healthFactor.getValue();
    }
}
