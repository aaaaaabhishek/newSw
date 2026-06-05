package com.MT_MX.demo.semantic.parser;

import java.util.*;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.RegulatoryReporting;

import java.util.List;

public class Field77BParser {

    public static List<RegulatoryReporting> parse(FieldNode field) {
        if (field == null) return null;

        List<RegulatoryReporting> result = new ArrayList<>();
        List<String> lines = field.getLines();

        for (String line : lines) {
            if (line == null || line.isEmpty()) continue;

            // Ensure line starts with "/"
            if (!line.startsWith("/")) continue;

            // Remove leading "/"
            String content = line.substring(1);

            // Split by "/"
            String[] parts = content.split("/");

            if (parts.length == 0) continue;

            String code = parts[0].trim();
            List<String> values = new ArrayList<>();

            // Remaining parts are values
            for (int i = 1; i < parts.length; i++) {
                if (!parts[i].isEmpty()) {
                    values.add(parts[i].trim());
                }
            }

            result.add(new RegulatoryReporting(code, values));
        }

        return result;
    }
}