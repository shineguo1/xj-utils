package org.xj.commons.web3j.protocol.core.decompile;


import org.xj.commons.toolkit.ObjectUtils;

/**
 * @author xj
 * @version 1.0.0 createTime:  2022/6/10 9:53
 */
public class CodesMap {

    private static Op[] codes = new Op[0xff + 1];

    static {
        codes[0x00] = new Op("STOP", 0, 0, 0, false);
        codes[0x01] = new Op("ADD", 3, 2, 1, false);
        codes[0x02] = new Op("MUL", 5, 2, 1, false);
        codes[0x03] = new Op("SUB", 3, 2, 1, false);
        codes[0x04] = new Op("DIV", 5, 2, 1, false);
        codes[0x05] = new Op("SDIV", 5, 2, 1, false);
        codes[0x06] = new Op("MOD", 5, 2, 1, false);
        codes[0x07] = new Op("SMOD", 5, 2, 1, false);
        codes[0x08] = new Op("ADDMOD", 8, 3, 1, false);
        codes[0x09] = new Op("MULMOD", 8, 3, 1, false);
        codes[0x0a] = new Op("EXP", 10, 2, 1, false);
        codes[0x0b] = new Op("SIGNEXTEND", 5, 2, 1, false);

        // 0x10 range - bit ops
        codes[0x10] = new Op("LT", 3, 2, 1, false);
        codes[0x11] = new Op("GT", 3, 2, 1, false);
        codes[0x12] = new Op("SLT", 3, 2, 1, false);
        codes[0x13] = new Op("SGT", 3, 2, 1, false);
        codes[0x14] = new Op("EQ", 3, 2, 1, false);
        codes[0x15] = new Op("ISZERO", 3, 1, 1, false);
        codes[0x16] = new Op("AND", 3, 2, 1, false);
        codes[0x17] = new Op("OR", 3, 2, 1, false);
        codes[0x18] = new Op("XOR", 3, 2, 1, false);
        codes[0x19] = new Op("NOT", 3, 1, 1, false);
        codes[0x1a] = new Op("BYTE", 3, 2, 1, false);

        // 0x20 range - crypto
        codes[0x20] = new Op("SHA3", 30, 2, 1, false);

        // 0x30 range - closure state
        codes[0x30] = new Op("ADDRESS", 2, 0, 1, true);
        codes[0x31] = new Op("BALANCE", 400, 1, 1, true, true);
        codes[0x32] = new Op("ORIGIN", 2, 0, 1, true);
        codes[0x33] = new Op("CALLER", 2, 0, 1, true);
        codes[0x34] = new Op("CALLVALUE", 2, 0, 1, true);
        codes[0x35] = new Op("CALLDATALOAD", 3, 1, 1, true);
        codes[0x36] = new Op("CALLDATASIZE", 2, 0, 1, true);
        codes[0x37] = new Op("CALLDATACOPY", 3, 3, 0, true);
        codes[0x38] = new Op("CODESIZE", 2, 0, 1, false);
        codes[0x39] = new Op("CODECOPY", 3, 3, 0, false);
        codes[0x3a] = new Op("GASPRICE", 2, 0, 1, false);
        codes[0x3b] = new Op("EXTCODESIZE", 700, 1, 1, true, true);
        codes[0x3c] = new Op("EXTCODECOPY", 700, 4, 0, true, true);
        codes[0x3d] = new Op("RETURNDATASIZE", 2, 0, 1, true);
        codes[0x3e] = new Op("RETURNDATACOPY", 3, 3, 0, true);

        // '0x40' range - block operations
        codes[0x40] = new Op("BLOCKHASH", 20, 1, 1, true, true);
        codes[0x41] = new Op("COINBASE", 2, 0, 1, true);
        codes[0x42] = new Op("TIMESTAMP", 2, 0, 1, true);
        codes[0x43] = new Op("NUMBER", 2, 0, 1, true);
        codes[0x44] = new Op("DIFFICULTY", 2, 0, 1, true);
        codes[0x45] = new Op("GASLIMIT", 2, 0, 1, true);

        // 0x50 range - 'storage' and execution
        codes[0x50] = new Op("POP", 2, 1, 0, false);
        codes[0x51] = new Op("MLOAD", 3, 1, 1, false);
        codes[0x52] = new Op("MSTORE", 3, 2, 0, false);
        codes[0x53] = new Op("MSTORE8", 3, 2, 0, false);
        codes[0x54] = new Op("SLOAD", 200, 1, 1, true, true);
        codes[0x55] = new Op("SSTORE", 0, 2, 0, true, true);
        codes[0x56] = new Op("JUMP", 8, 1, 0, false);
        codes[0x57] = new Op("JUMPI", 10, 2, 0, false);
        codes[0x58] = new Op("PC", 2, 0, 1, false);
        codes[0x59] = new Op("MSIZE", 2, 0, 1, false);
        codes[0x5a] = new Op("GAS", 2, 0, 1, false);
        codes[0x5b] = new Op("JUMPDEST", 1, 0, 0, false);

        // 0x60, range
        codes[0x60] = new Op("PUSH1", 3, 0, 1, false);
        codes[0x61] = new Op("PUSH2", 3, 0, 1, false);
        codes[0x62] = new Op("PUSH3", 3, 0, 1, false);
        codes[0x63] = new Op("PUSH4", 3, 0, 1, false);
        codes[0x64] = new Op("PUSH5", 3, 0, 1, false);
        codes[0x65] = new Op("PUSH6", 3, 0, 1, false);
        codes[0x66] = new Op("PUSH7", 3, 0, 1, false);
        codes[0x67] = new Op("PUSH8", 3, 0, 1, false);
        codes[0x68] = new Op("PUSH9", 3, 0, 1, false);
        codes[0x69] = new Op("PUSH10", 3, 0, 1, false);
        codes[0x6a] = new Op("PUSH11", 3, 0, 1, false);
        codes[0x6b] = new Op("PUSH12", 3, 0, 1, false);
        codes[0x6c] = new Op("PUSH13", 3, 0, 1, false);
        codes[0x6d] = new Op("PUSH14", 3, 0, 1, false);
        codes[0x6e] = new Op("PUSH15", 3, 0, 1, false);
        codes[0x6f] = new Op("PUSH16", 3, 0, 1, false);
        codes[0x70] = new Op("PUSH17", 3, 0, 1, false);
        codes[0x71] = new Op("PUSH18", 3, 0, 1, false);
        codes[0x72] = new Op("PUSH19", 3, 0, 1, false);
        codes[0x73] = new Op("PUSH20", 3, 0, 1, false);
        codes[0x74] = new Op("PUSH21", 3, 0, 1, false);
        codes[0x75] = new Op("PUSH22", 3, 0, 1, false);
        codes[0x76] = new Op("PUSH23", 3, 0, 1, false);
        codes[0x77] = new Op("PUSH24", 3, 0, 1, false);
        codes[0x78] = new Op("PUSH25", 3, 0, 1, false);
        codes[0x79] = new Op("PUSH26", 3, 0, 1, false);
        codes[0x7a] = new Op("PUSH27", 3, 0, 1, false);
        codes[0x7b] = new Op("PUSH28", 3, 0, 1, false);
        codes[0x7c] = new Op("PUSH29", 3, 0, 1, false);
        codes[0x7d] = new Op("PUSH30", 3, 0, 1, false);
        codes[0x7e] = new Op("PUSH31", 3, 0, 1, false);
        codes[0x7f] = new Op("PUSH32", 3, 0, 1, false);

        codes[0x80] = new Op("DUP1", 3, 0, 1, false);
        codes[0x81] = new Op("DUP2", 3, 0, 1, false);
        codes[0x82] = new Op("DUP3", 3, 0, 1, false);
        codes[0x83] = new Op("DUP4", 3, 0, 1, false);
        codes[0x84] = new Op("DUP5", 3, 0, 1, false);
        codes[0x85] = new Op("DUP6", 3, 0, 1, false);
        codes[0x86] = new Op("DUP7", 3, 0, 1, false);
        codes[0x87] = new Op("DUP8", 3, 0, 1, false);
        codes[0x88] = new Op("DUP9", 3, 0, 1, false);
        codes[0x89] = new Op("DUP10", 3, 0, 1, false);
        codes[0x8a] = new Op("DUP11", 3, 0, 1, false);
        codes[0x8b] = new Op("DUP12", 3, 0, 1, false);
        codes[0x8c] = new Op("DUP13", 3, 0, 1, false);
        codes[0x8d] = new Op("DUP14", 3, 0, 1, false);
        codes[0x8e] = new Op("DUP15", 3, 0, 1, false);
        codes[0x8f] = new Op("DUP16", 3, 0, 1, false);
        codes[0x90] = new Op("SWAP1", 3, 0, 0, false);
        codes[0x91] = new Op("SWAP2", 3, 0, 0, false);
        codes[0x92] = new Op("SWAP3", 3, 0, 0, false);
        codes[0x93] = new Op("SWAP4", 3, 0, 0, false);
        codes[0x94] = new Op("SWAP5", 3, 0, 0, false);
        codes[0x95] = new Op("SWAP6", 3, 0, 0, false);
        codes[0x96] = new Op("SWAP7", 3, 0, 0, false);
        codes[0x97] = new Op("SWAP8", 3, 0, 0, false);
        codes[0x98] = new Op("SWAP9", 3, 0, 0, false);
        codes[0x99] = new Op("SWAP10", 3, 0, 0, false);
        codes[0x9a] = new Op("SWAP11", 3, 0, 0, false);
        codes[0x9b] = new Op("SWAP12", 3, 0, 0, false);
        codes[0x9c] = new Op("SWAP13", 3, 0, 0, false);
        codes[0x9d] = new Op("SWAP14", 3, 0, 0, false);
        codes[0x9e] = new Op("SWAP15", 3, 0, 0, false);
        codes[0x9f] = new Op("SWAP16", 3, 0, 0, false);

        codes[0xa0] = new Op("LOG0", 375, 2, 0, false);
        codes[0xa1] = new Op("LOG1", 375, 3, 0, false);
        codes[0xa2] = new Op("LOG2", 375, 4, 0, false);
        codes[0xa3] = new Op("LOG3", 375, 5, 0, false);
        codes[0xa4] = new Op("LOG4", 375, 6, 0, false);

        // '0xf0' range - closures
        codes[0xf0] = new Op("CREATE", 32000, 3, 1, true, true);
        codes[0xf1] = new Op("CALL", 700, 7, 1, true, true);
        codes[0xf2] = new Op("CALLCODE", 700, 7, 1, true, true);
        codes[0xf3] = new Op("RETURN", 0, 2, 0, false);
        codes[0xf4] = new Op("DELEGATECALL", 700, 6, 1, true, true);
        codes[0xfa] = new Op("STATICCALL", 700, 6, 1, true, true);
        codes[0xfd] = new Op("REVERT", 0, 2, 0, false);

        // '0x70', range - other
        codes[0xfe] = new Op("INVALID", 0, 0, 0, false);
        codes[0xff] = new Op("SELFDESTRUCT", 5000, 1, 0, false, true);

    }

    public static Op get(int index) {
        Op invalid = new Op("INVALID", 0, 0, 0, false, false);
        if (index <= 0xff && index >= 0) {
            return ObjectUtils.defaultIfNull(codes[index], invalid);
        }
        return invalid;
    }
}
