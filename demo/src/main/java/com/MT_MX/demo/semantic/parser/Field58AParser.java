package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;

public class Field58AParser {
    private Field58AParser(){}
    public static String parse(FieldNode fieldNode){
        if(fieldNode!=null) return null;
        String bic=fieldNode.getValue();
        return bic.trim();
    }
}
