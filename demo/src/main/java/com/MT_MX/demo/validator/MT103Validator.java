package com.MT_MX.demo.validator;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import com.MT_MX.demo.config.ConditionalRuleEngine;
import com.MT_MX.demo.rule.RuleLoader;
import com.MT_MX.demo.rule.ConditionalRule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
public class MT103Validator implements MTValidator {
  private final RuleLoader ruleLoader;

    public MT103Validator(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
    }
    private static final List<String> MANDATORY_FIELDS =
            List.of("20","23B","32A","59","71A");

    @Override
    public void validate(SwiftAst ast) {

        List<ValidationError> errors = new ArrayList<>();

        BlockNode block4 = ast.getBlock(4);
        if (block4 == null) {
            throw new RuntimeException("Block 4 missing");
        }

        
        for (String tag : MANDATORY_FIELDS) {
            if (block4.getField(tag) == null) {
                errors.add(new ValidationError(
                        tag, "T01", "Mandatory field missing"));
            }
        }
        checkOrderingCustomerGroup(block4, errors);

        DuplicateFieldValidator.checkNoDuplicates(block4, errors);

        check23B(ast, errors);
        check71A(ast, errors);
        check32A(ast, errors);

        System.out.println(
                getClass().getClassLoader().getResource("mt103-rules.yaml")
        );
        List<ConditionalRule> rules = ruleLoader.load("mt103-rules.yaml");

        if (rules == null || rules.isEmpty()) {
            throw new IllegalStateException("No MT103 rules loaded. Check YAML path and parsing.");
        }

        ConditionalRuleEngine engine = new ConditionalRuleEngine(rules);

        if (ast == null) {
            throw new IllegalStateException("AST is null - parsing failed before validation");
        }

        engine.run(ast, errors);        // -------------------------
        if (!errors.isEmpty()) {
            errors.forEach(System.out::println);
            throw new RuntimeException("MT103 validation failed: " + errors);
        }

        System.out.println("MT103 validation passed.");
    }

    private void checkOrderingCustomerGroup(
            BlockNode block4,
            List<ValidationError> errors
    ) {
        boolean exists =
                block4.getField("50A") != null ||
                        block4.getField("50F") != null ||
                        block4.getField("50K") != null;

        if (!exists) {
            errors.add(new ValidationError(
                    "50x",
                    "GRP01",
                    "One of 50A / 50F / 50K required"
            ));
        }
    }

    
private void check23B(SwiftAst ast, List<ValidationError> errors) {
    FieldNode f = ast.getField("23B");
    if (f == null) return;

    String value = f.getValue().trim();

    if (!"CRED".equals(value)) {
        errors.add(new ValidationError(
                "23B", "T23B",
                "Invalid operation code (must be CRED)"
        ));
    }
}
private void check71A(SwiftAst ast, List<ValidationError> errors) {
    FieldNode f = ast.getField("71A");
    if (f == null) return;

    String rawValue = f.getValue();
    if (rawValue == null) {
        errors.add(new ValidationError("71A", "T71A", "Missing charges code"));
        return;
    }

    StringBuilder sb = new StringBuilder();
    for (char c : rawValue.toCharArray()) {
        if (Character.isLetter(c)) {
            sb.append(c);
        }
    }

    String value = sb.toString().toUpperCase();  // Normalize to uppercase


    List<String> allowed = List.of("OUR", "SHA", "BEN");

    if (!allowed.contains(value)) {
        errors.add(new ValidationError(
                "71A",
                "T71A",
                "Invalid charges code (must be OUR, SHA, or BEN). Found: '" + rawValue + "'"
        ));
    }
}

private void check32A(SwiftAst ast, List<ValidationError> errors) {
    FieldNode f = ast.getField("32A");
    if (f == null) return;

    String value = f.getValue().trim();

    if (value.length() < 10) {
        errors.add(new ValidationError(
                "32A", "T32A",
                "Field too short to contain date, currency, and amount"
        ));
        return;
    }

    String datePart = value.substring(0, 6);
    String currencyPart = value.substring(6, 9);
    String amountPart = value.substring(9).replace(',', '.'); // normalize decimal

    // Validate date 
    try {
        java.time.LocalDate.parse(datePart, java.time.format.DateTimeFormatter.ofPattern("yyMMdd"));
    } catch (Exception e) {
        errors.add(new ValidationError(
                "32A", "T32A",
                "Invalid date format in 32A: " + datePart
        ));
    }

    //  Validate currency (3 uppercase letters)
    if (currencyPart.length() != 3 || !currencyPart.chars().allMatch(Character::isUpperCase)) {
        errors.add(new ValidationError(
                "32A", "T32A",
                "Invalid currency code in 32A: " + currencyPart
        ));
    }

    // Validate amount (must be a valid decimal number)
    try {
        new java.math.BigDecimal(amountPart);
    } catch (Exception e) {
        errors.add(new ValidationError(
                "32A", "T32A",
                "Invalid amount in 32A: " + amountPart
        ));
    }
}
}
