package com.MT_MX.demo.validator;

import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.ast.SwiftMxAst;

public interface MXValidator {
    /**
     * Validate the given AST according to MT type rules.
     * @param ast Parsed SWIFT message
     * @throws RuntimeException if validation fails
     */
    void validate(SwiftMxAst ast);
}
