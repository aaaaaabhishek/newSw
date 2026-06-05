package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.OrderingCustomer;

import java.util.ArrayList;
import java.util.List;

public class Field50FParser {
private Field50FParser(){}

    public static OrderingCustomer parse(FieldNode field) {
        if (field == null) return null;
        String account = null;
        String name=null;
        List<String> addrees = new ArrayList<>();
        List<String> lines = field.getLines();
        if (lines.get(0).startsWith("/")) {
            account = lines.get(0).substring(1);
        }
        for (String line : lines) {
            if (line.startsWith("1/")) name = line.substring(2);
            else if (line.startsWith("2/") || line.startsWith("3/") || line.startsWith("4/"))
                addrees.add(line.substring(2));
        }
   return new OrderingCustomer(name,account,addrees);
    }
}
