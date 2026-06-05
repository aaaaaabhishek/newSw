package com.MT_MX.demo.semantic.parser;

public class Field72Instruction {

    private String code;
    private String instruction;

    public Field72Instruction(String code, String instruction) {
        this.code = code;
        this.instruction = instruction;
    }

    public String getCode() { return code; }
    public String getInstruction() { return instruction; }

    @Override
    public String toString() {
        return code + " -> " + instruction;
    }
}