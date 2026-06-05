package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.BeneficiaryCustomer;

import java.util.ArrayList;
import java.util.List;

public class Field59Parser {

    public static BeneficiaryCustomer parse(FieldNode field) {
        if (field == null) return null;

        List<String> lines = field.getLines();

        String account = null;
        String name = "";
        List<String> address = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("/")) {
                account = line.substring(1);
                continue;
            }

            if (name.isEmpty()) {
                name = line.trim();
            } else {
                address.add(line.trim());
            }
        }

        return new BeneficiaryCustomer(account, name, address);
    }
}