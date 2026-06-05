package com.MT_MX.demo.semantic.parser;

public class SwiftBlocks {
    private String block1;
    private String block2;
    private String block3;
    private String block4;
    private String block5;

    public String getBlock1() {
        return block1; }
    public void setBlock1(String block1) {
        this.block1 = block1;
    }

    public String getBlock2() {
        return block2;
    }
    public void setBlock2(String block2) {
        this.block2 = block2;
    }
    public String getBlock3() {
        return block3;
    }
    public void setBlock3(String block3) {
        this.block3 = block3;
    }

    public String getBlock4() {
        return block4;
    }
    public void setBlock4(String block4) {
        this.block4 = block4;
    }

    public String getBlock5() {
        return block5;
    }
    public void setBlock5(String block5) {
        this.block5 = block5;
    }

    public String getBlock(int blockNo) {
        return switch (blockNo) {
            case 1 -> block1;
            case 2 -> block2;
            case 3 -> block3;
            case 4 -> block4;
            case 5 -> block5;
            default -> null;
        };
    }
}
