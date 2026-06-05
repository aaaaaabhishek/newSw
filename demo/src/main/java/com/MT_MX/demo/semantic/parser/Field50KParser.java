package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.OrderingCustomer;

import java.util.Collections;
import java.util.List;

public class Field50KParser {

    public static OrderingCustomer parse(FieldNode field) {

        if (field == null) return null;

        List<String> lines = field.getLines();
        if (lines.isEmpty()) return null;

        String account = null;
        int idx = 0;

        if (lines.get(0).startsWith("/")) {
            account = lines.get(0).substring(1);
            idx = 1;
        }

        String name = idx < lines.size() ? lines.get(idx) : "";
        List<String> address =
                idx + 1 < lines.size()
                        ? lines.subList(idx + 1, lines.size())
                        : Collections.emptyList();

        return new OrderingCustomer(account, name, address);
    }
}