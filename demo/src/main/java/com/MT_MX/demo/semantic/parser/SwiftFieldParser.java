package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;

public final class SwiftFieldParser {

    public BlockNode parseBlock(int blockNumber, String blockContent) throws Exception {
        BlockNode blockNode = new BlockNode(blockNumber);
        FieldNode currentField = null;
        blockNode.setContent(blockContent);
        String[] lines = blockContent.split("\\r?\\n");
        for (String line : lines) {
            if (line.startsWith(":")) {
                if (currentField != null) {
                        blockNode.addField(currentField);
                }
                int idx = line.indexOf(':', 1);
                if (idx == -1) {
                    throw  new Exception("Invalid field format: " + line);
                }
                String tag = line.substring(1, idx);
                currentField = new FieldNode(tag);
                currentField.addLine(line.substring(idx + 1));
            } else if (currentField != null) {
                currentField.addLine(line);
            }
        }
        if (currentField != null) {
            blockNode.addField(currentField);
        }

        return blockNode;
    }
    public BlockNode parseBlock3(int blockNumber, String content) {

        BlockNode blockNode = new BlockNode(blockNumber);
        blockNode.setContent(content);

        int i = 0;

        while (i < content.length()) {

            if (content.charAt(i) == '{') {

                int end = findMatchingBrace(content, i);

                String fieldContent = content.substring(i + 1, end);

                int colonIndex = fieldContent.indexOf(':');
                if (colonIndex != -1) {

                    String tag = fieldContent.substring(0, colonIndex);
                    String value = fieldContent.substring(colonIndex + 1);

                    FieldNode field = new FieldNode(tag);
                    field.addLine(value);
                    blockNode.addField(field);
                }

                i = end + 1;

            } else {
                i++;
            }
        }

        return blockNode;
    }
    private int findMatchingBrace(String s, int start) {
        int depth = 0;
        for (int i = start; i < s.length(); i++) {
            if (s.charAt(i) == '{') depth++;
            if (s.charAt(i) == '}') depth--;
            if (depth == 0) return i;
        }
        throw new RuntimeException("Unbalanced braces in SWIFT message");
    }
}
