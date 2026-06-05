package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;

public class Field54AParser {
    private Field54AParser(){}
    public static String parse(FieldNode field) {

        if (field == null) return null;

        return field.getValue().trim();
    }
}
