package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;

public class Field53AParser {
    private Field53AParser(){}
    public static String parse(FieldNode fieldNode){
        if(fieldNode==null) return null;
        return fieldNode.getValue().trim();
    }
}
