package com.MT_MX.demo.validator;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.rule.ConditionalRule;

import java.util.List;

public class ConditionalValidator {

    private final List<ConditionalRule> rules;

    public ConditionalValidator(List<ConditionalRule> rules) {
        this.rules = rules;
    }

    public void validate(
            SwiftAst ast,
            List<ValidationError> errors
    ) {
        for (ConditionalRule rule : rules) {
            rule.apply(ast, errors);
        }
    }
}