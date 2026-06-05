package com.MT_MX.demo.semantic.parser;

import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.semantic.Field32AValue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Field32AParser {

    private static final DateTimeFormatter YYMMDD =
            DateTimeFormatter.ofPattern("yyMMdd");

    public static Field32AValue parse(FieldNode field) {

        if (field == null) return null;

        String v = field.getValue().trim();

        String datePart = v.substring(0, 6);
        String currency = v.substring(6, 9);
        String amountStr = v.substring(9).trim();
        amountStr = amountStr.replace(',', '.');
        LocalDate date = LocalDate.parse(datePart, YYMMDD);
        BigDecimal amount = new BigDecimal(amountStr);

        return new Field32AValue(date, currency, amount);
    }
}