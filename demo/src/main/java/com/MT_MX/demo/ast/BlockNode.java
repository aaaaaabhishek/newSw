package com.MT_MX.demo.ast;

import java.util.ArrayList;
import java.util.List;

public final class BlockNode {

    private final int blockNumber;
    private final List<FieldNode> fields = new ArrayList<>();
    private String content; // raw block content

    public BlockNode(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public void addField(FieldNode field) {
        if (field != null) {
            fields.add(field);
        }
    }

    public int getBlockNumber(){
        return blockNumber;
       }

    public List<FieldNode> getFields() {
        return List.copyOf(fields); // immutable copy
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // -------------------------
    // Convenience methods
    // -------------------------

    // Get a field by tag
    public FieldNode getField(String tag) {
        if (tag == null || tag.isEmpty()) return null;
        for (FieldNode field : fields) {
            if (tag.equals(field.getTag())) {
                return field;
            }
        }
        return null;
    }

    // Get field value (all lines concatenated)
    public String getFieldValue(String tag) {
        FieldNode field = getField(tag);
        if (field == null || field.getLines().isEmpty()) return null;
        return String.join("\n", field.getLines());
    }
}
