package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.OrderingInstitution;
import com.MT_MX.demo.semantic.OrderingInstitutionIdentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field52BParser {

    public static OrderingInstitutionIdentifier parse(FieldNode field) {
            if (field == null) return null;

            List<String> lines = field.getLines();
            if (lines.isEmpty()) return null;

            String account = null;
            int idx = 0;

            if (lines.get(0).startsWith("/")) {
                account = lines.get(0).substring(1);
                idx = 1;
            }

            String bic = idx < lines.size() ? lines.get(idx) : "";
            idx++;
            String location =idx < lines.size() ? lines.get(idx) : "";
            return new OrderingInstitutionIdentifier(account,bic,location);
        }
    }
