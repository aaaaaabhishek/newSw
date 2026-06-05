package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;

public class Field56AParser {
    private Field56AParser(){}
    public static String parse(FieldNode fieldNode){
        if(fieldNode!=null) return null;
        String bic=fieldNode.getValue();
        return bic.trim();
    }
}
