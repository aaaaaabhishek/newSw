package com.MT_MX.demo.ast;
import java.util.ArrayList;
import java.util.List;
// Represents a field within a block
public final class FieldNode {
    private final String tag;
    private final List<String> lines = new ArrayList<>();

    public FieldNode(String tag) {
        this.tag = tag;
    }
    public void addLine(String line) {
        lines.add(line);
    }
    public String getTag() {
        return tag;
    }
    public List<String> getLines() {
        return lines;
        // return List.copyOf(lines);
    }


    public String getValue() {
        return String.join("\n", lines);
    }
}