package io.github.applecommander.disassembler.api.sweet16;

import io.github.applecommander.disassembler.api.Instruction;

public class InstructionSWEET16 implements Instruction {
    private AddressModeSWEET16 addressMode;
    private OpcodeSWEET16 opcode;
    private int address;
    private int register;
    private byte[] code;
    
    InstructionSWEET16(AddressModeSWEET16 addressMode, OpcodeSWEET16 opcode, int register, int address, byte[] code) {
        this.addressMode = addressMode;
        this.opcode = opcode;
        this.register = register;
        this.address = address;
        this.code = code;
    }

    @Override
    public int getAddress() {
        return address;
    }

    @Override
    public int getLength() {
        return addressMode.getInstructionLength();
    }

    @Override
    public byte[] getBytes() {
        return code;
    }

    @Override
    public String getOpcodeMnemonic() {
        return opcode.getMnemonic();
    }

    @Override
    public boolean operandHasAddress() {
        return addressMode.isOperandAbsoluteAddress() || addressMode.isOperandRelativeAddress();
    }

    @Override
    public int getOperandValue() {
        switch (getLength()) {
        case 3:
            return Byte.toUnsignedInt(code[1]) + Byte.toUnsignedInt(code[2])*256;
        case 2:
            if (addressMode.isOperandRelativeAddress()) {
                return address + 2 + code[1];   // allow sign extension
            }
            else {
                return Byte.toUnsignedInt(code[1]);
            }
        default:
            return 0;
        }
    }

    @Override
    public String formatOperandWithValue() {
        String label = "-";
        if (addressMode.isOperandAbsoluteAddress() || addressMode.isOperandRelativeAddress()|| getLength() == 3) {
            label = String.format("%04X", getOperandValue());
        }
        else if (getLength() == 2) {
            label = String.format("%02X",getOperandValue());
        }
        return formatOperandWithLabel(label);
    }

    @Override
    public String formatOperandWithLabel(String label) {
        if (addressMode.doesOperandRequireRegister()) {
            if (getLength() == 1) {
                return String.format(addressMode.getInstructionFormat(), getOpcodeMnemonic(), register);
            }
            return String.format(addressMode.getInstructionFormat(), getOpcodeMnemonic(), register, label);
        }
        else {
            if (getLength() == 1) {
                return String.format(addressMode.getInstructionFormat(), getOpcodeMnemonic());
            }
            return String.format(addressMode.getInstructionFormat(), getOpcodeMnemonic(), label);
        }
    }
}