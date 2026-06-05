package com.MT_MX.demo.config;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.rule.ConditionalRule;

import java.util.List;

public class ConditionalRuleEngine {

    private final List<ConditionalRule> rules;

    public ConditionalRuleEngine(List<ConditionalRule> rules) {
        this.rules = rules;
    }

    public void run(
            SwiftAst ast,
            List<ValidationError> errors
    ) {
        for (ConditionalRule r : rules) {
            r.apply(ast, errors);
        }
    }
}