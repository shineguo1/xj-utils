package model;

import org.xj.commons.web3j.protocol.core.decompile.ton.address.TonAddress;
import org.xj.commons.web3j.protocol.core.decompile.ton.boc.Cell;
import lombok.Data;

import java.math.BigInteger;

/**
 * @author xj
 * @version 1.0.0 createTime:  2024/1/2 15:21
 */
@Data
public class PoolFullData {

    private int state;
    private Boolean halted;
    private BigInteger total_balance;
    private BigInteger interest_rate;
    private BigInteger optimistic_deposit_withdrawals;
    private Boolean deposits_open;
    //num->String 格式为无前缀hex
    private String saved_validator_set_hash;
    private RoundBorrowers prev_round_borrowers;
    private RoundBorrowers current_round_borrowers;
    private BigInteger min_loan_per_validator;
    private BigInteger max_loan_per_validator;
    private BigInteger governance_fee;
    private TonAddress jetton_minter;


    //兼容decoded的JSONObject，补充String类型的set方法
    //=========================================
    public void setJetton_minter(String jetton_minter) {
        this.jetton_minter = TonAddress.create(jetton_minter);
    }


    @Data
    public static class RoundBorrowers {
        private Cell[] borrowers_dict;
        private BigInteger round_id;
        private BigInteger active_borrowers;
        private BigInteger borrowed;
        private BigInteger expected;
        private BigInteger returned;
        private BigInteger profit;

        //兼容decoded的JSONObject，补充String类型的set方法
        //=========================================
        public void setBorrowers_dict(String hex) {
            this.borrowers_dict = Cell.fromHex(hex);
        }
    }
}
