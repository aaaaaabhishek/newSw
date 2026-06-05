package com.MT_MX.demo.semantic.parser;

public final class ApplicationHeaderBlock {

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    String content;
//    public ApplicationHeaderBlock(String content) {
//
//        this.direction = content.substring(0, 1);
//        this.messageType = content.substring(1, 4);
//        this.receiverBic = content.substring(4, 16);
//        this.priority = content.substring(16);
//    }
}