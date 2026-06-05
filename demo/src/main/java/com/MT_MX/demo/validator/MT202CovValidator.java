package com.MT_MX.demo.validator;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.config.ConditionalRuleEngine;
import com.MT_MX.demo.rule.ConditionalRule;
import com.MT_MX.demo.rule.RuleLoader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class MT202CovValidator implements MTValidator{
    private final RuleLoader ruleLoader;

    public MT202CovValidator(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    @Override
    public void validate(SwiftAst ast) {
        List<ValidationError> errors=new ArrayList<>();

        List<ConditionalRule> rules = ruleLoader.load("mt202COV-rules.yaml");
        ConditionalRuleEngine engine = new ConditionalRuleEngine(rules);
        engine.run(ast, errors);
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            throw new RuntimeException("MT202Cov validation failed: " + errors);
        }
    }
}
