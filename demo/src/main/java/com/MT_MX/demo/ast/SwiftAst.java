package com.MT_MX.demo.ast;

import java.util.ArrayList;
import java.util.List;

// Root AST object for a SWIFT message
public class SwiftAst {
    private final List<BlockNode> blocks = new ArrayList<>();

    public void addBlock(BlockNode block) {
        blocks.add(block);
    }

    public List<BlockNode> getBlocks() {
        return blocks;
    }

    public BlockNode getBlock(int blockNumber) {
        return blocks.stream()
                .filter(b -> b.getBlockNumber() == blockNumber)
                .findFirst()
                .orElse(null);
    }

    public boolean hasField(String tag) {
        return blocks.stream()
                .flatMap(b -> b.getFields().stream())
                .anyMatch(f -> f.getTag().equals(tag));
    }

    // Get the first field by tag across all blocks
    public FieldNode getField(String tag) {
        if (tag == null || tag.isEmpty()) return null;
        for (BlockNode block : blocks) {
            FieldNode field = block.getField(tag);
            if (field != null) return field;
        }
        return null; // not found
    }
    public List<FieldNode> getFieldsByPrefix(String prefix) {
        List<FieldNode> result = new ArrayList<>();

        if (prefix == null || prefix.isEmpty()) {
            return result;
        }

        for (BlockNode block : blocks) {
            for (FieldNode field : block.getFields()) {
                if (field.getTag() != null && field.getTag().startsWith(prefix)) {
                    result.add(field);
                }
            }
        }

        return result;
    }
}

