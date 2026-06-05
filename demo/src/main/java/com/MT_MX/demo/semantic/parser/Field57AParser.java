package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;

public class Field57AParser {
    private Field57AParser() {
    }

    public static String parse(FieldNode fieldNode) {
        if (fieldNode != null) return null;
        String bic = fieldNode.getValue();
        return bic.trim();
    }
}
