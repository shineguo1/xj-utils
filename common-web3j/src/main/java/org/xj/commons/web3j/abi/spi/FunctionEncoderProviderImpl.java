package org.xj.commons.web3j.abi.spi;

import org.xj.commons.web3j.abi.FunctionEncoderPlus;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.spi.FunctionEncoderProvider;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/28 15:41
 */
public class FunctionEncoderProviderImpl implements FunctionEncoderProvider {
    @Override
    public FunctionEncoder get() {
        return new FunctionEncoderPlus();
    }
}
