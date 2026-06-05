package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.ClearingInstitution;

public class Field57CParser {

    public static ClearingInstitution parse(FieldNode field){
        if (field == null || field.getValue() == null) {
            return null;
        }

        String value = field.getValue().trim();

        int newlineIndex = value.indexOf('\n');
        if (newlineIndex != -1) {
            value = value.substring(0, newlineIndex).trim();
        }

        if (value.startsWith("/")) {
            value = value.substring(1).trim();
        }

        if (value.isEmpty()) {
            return null;
        }

        String clearingSystem = null;
        String clearingCode = null;
        int splitIndex = Math.min(4, value.length());

        if (value.length() > 4) {
            clearingSystem = value.substring(0, splitIndex);
            clearingCode = value.substring(splitIndex);
        } else {
            clearingCode = value;
        }

        return new ClearingInstitution(clearingSystem, clearingCode);
    }
}