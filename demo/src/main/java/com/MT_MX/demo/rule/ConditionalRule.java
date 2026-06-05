package com.MT_MX.demo.rule;
import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import java.util.List;

public interface ConditionalRule {
    void apply(SwiftAst ast, List<ValidationError> errors);
}