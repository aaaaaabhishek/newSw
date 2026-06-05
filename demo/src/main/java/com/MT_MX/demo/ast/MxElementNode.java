package com.MT_MX.demo.ast;

import java.util.*;

public final class MxElementNode {

    private final String name;
    private String text;

    private final Map<String,String> attributes = new HashMap<>();
    private final List<MxElementNode> children = new ArrayList<>();

    public MxElementNode(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void addAttribute(String k, String v) {
        attributes.put(k, v);
    }

    public Map<String,String> getAttributes() {
        return attributes;
    }

    public void addChild(MxElementNode child) {
        children.add(child);
    }

    public List<MxElementNode> getChildren() {
        return children;
    }

    // ---------- helpers ----------

    public MxElementNode getChild(String name) {
        for (MxElementNode c : children) {
            if (name.equals(c.name)) return c;
        }
        return null;
    }

    public List<MxElementNode> getChildren(String name) {
        List<MxElementNode> list = new ArrayList<>();
        for (MxElementNode c : children) {
            if (name.equals(c.name)) list.add(c);
        }
        return list;
    }
    public MxElementNode findPath(String... path) {
        MxElementNode cur = this;
        for (String p : path) {
            if (cur == null) return null;
            cur = cur.getChild(p);
        }
        return cur;
    }
}