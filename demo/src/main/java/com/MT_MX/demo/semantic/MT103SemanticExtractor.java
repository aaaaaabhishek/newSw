package com.MT_MX.demo.semantic;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.semantic.parser.Field32AParser;
import com.MT_MX.demo.semantic.parser.Field50KParser;
import com.MT_MX.demo.semantic.parser.Field59Parser;

public class MT103SemanticExtractor {

    public static Field32AValue extract32A(SwiftAst ast) {
        return Field32AParser.parse(ast.getField("32A"));
    }

    public static OrderingCustomer extract50K(SwiftAst ast) {
        return Field50KParser.parse(ast.getField("50K"));
    }

    public static BeneficiaryCustomer extract59(SwiftAst ast) {
        return Field59Parser.parse(ast.getField("59"));
    }
}