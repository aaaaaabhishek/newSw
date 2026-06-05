package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.BeneficiaryInstitution;
import com.MT_MX.demo.semantic.OrderingInstitution;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Field58DParser {

    public static BeneficiaryInstitution parse(FieldNode field){
        if (field == null) return null;

        List<String> lines = field.getLines()
                .stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        if (lines.isEmpty()) return null;

        String account = null;
        int idx = 0;

        if (lines.get(0).startsWith("/")) {
            account = lines.get(0).substring(1).trim();
            idx = 1;
        }

        String name = idx < lines.size() ? lines.get(idx).trim() : "";

        List<String> address =
                idx + 1 < lines.size()
                        ? lines.subList(idx + 1, lines.size())
                        .stream()
                        .map(String::trim)
                        .collect(Collectors.toList())
                        : Collections.emptyList();

        return new BeneficiaryInstitution(account, name, address);
    }
}