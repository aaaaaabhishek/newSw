package com.MT_MX.demo.validator;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.config.ConditionalRuleEngine;
import com.MT_MX.demo.rule.ConditionalRule;
import com.MT_MX.demo.rule.RuleLoader;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class MT202Validator implements MTValidator{
    private static final List<String> MANDATORY_FIELDS =
            List.of("20","32A","58A");
    private final RuleLoader ruleLoader;

    public MT202Validator(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }

    @Override
    public void validate(SwiftAst ast) {
       List<ValidationError> errors=new ArrayList<>();
        BlockNode block4=ast.getBlock(4);
        if (block4 == null) {
            throw new RuntimeException("Block 4 missing");
        }
        // -------------------------
        // Mandatory Fields
        // -------------------------
        for(String tag:MANDATORY_FIELDS){
           if(block4.getFieldValue(tag)==null){
               errors.add(new ValidationError(
                       tag, "T01", "Mandatory field missing"));
           }
       }


        // =========================
        // Dynamic Conditional Rules (YAML driven)
        // ========================
        List<ConditionalRule> rules = ruleLoader.load("mt202-rules.yaml");
        ConditionalRuleEngine engine = new ConditionalRuleEngine(rules);
        engine.run(ast, errors);
        // -------------------------
        // Final Result
        // -------------------------
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            throw new RuntimeException("MT202 validation failed: " + errors);
        }


    }
}
