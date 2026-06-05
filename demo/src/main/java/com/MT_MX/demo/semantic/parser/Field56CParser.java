package com.MT_MX.demo.semantic.parser;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.ClearingInstitution;

public class Field56CParser {

    public static ClearingInstitution parse(FieldNode field){
        if (field == null || field.getValue() == null) {
            return null;
        }

        String value = field.getValue().trim();

        if (value.startsWith("/")) {
            value = value.substring(1).trim();
        }

        String clearingSystem = null;
        String clearingCode = null;
        if (value.length() > 4) {
            clearingSystem = value.substring(0, 4);
            clearingCode = value.substring(4);
        } else {
            clearingCode = value;
        }

        return new ClearingInstitution(clearingSystem, clearingCode);
    }
}