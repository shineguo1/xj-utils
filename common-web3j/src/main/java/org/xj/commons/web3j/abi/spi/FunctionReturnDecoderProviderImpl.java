package org.xj.commons.web3j.abi.spi;

import org.xj.commons.web3j.abi.FunctionReturnDecoderPlus;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.spi.FunctionReturnDecoderProvider;

/**
 * @author xj
 * @version 1.0.0 createTime:  2023/2/28 15:41
 */
public class FunctionReturnDecoderProviderImpl implements FunctionReturnDecoderProvider {
    @Override
    public FunctionReturnDecoder get() {
        return new FunctionReturnDecoderPlus();
    }
}
