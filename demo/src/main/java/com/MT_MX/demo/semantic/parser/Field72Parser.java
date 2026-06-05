package com.MT_MX.demo.semantic.parser;


import com.MT_MX.demo.ast.FieldNode;

import java.util.ArrayList;
import java.util.List;

public class Field72Parser {

    public static List<Field72Instruction> parse(FieldNode field72) {

        List<Field72Instruction> instructions = new ArrayList<>();

        if (field72 == null) {
            return instructions;
        }

        for (String line : field72.getLines()) {

            if (!line.startsWith("/")) {
                continue;
            }

            int secondSlash = line.indexOf("/", 1);

            if (secondSlash == -1) {
                continue;
            }

            String code = line.substring(1, secondSlash);

            String text = "";
            if (secondSlash + 1 < line.length()) {
                text = line.substring(secondSlash + 1);
            }

            instructions.add(new Field72Instruction(code, text));
        }

        return instructions;
    }
}