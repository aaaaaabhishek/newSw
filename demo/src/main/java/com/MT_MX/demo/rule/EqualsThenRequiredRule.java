package com.MT_MX.demo.rule;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import org.springframework.stereotype.Component;

import java.util.List;

public class EqualsThenRequiredRule implements ConditionalRule {

    private final String ifTag;
    private final String ifValue;
    private final String requiredTag;

    public EqualsThenRequiredRule(
            String ifTag,
            String ifValue,
            String requiredTag
    ) {
        this.ifTag = ifTag;
        this.ifValue = ifValue;
        this.requiredTag = requiredTag;
    }

    @Override
    public void apply(SwiftAst ast, List<ValidationError> errors) {

        FieldNode f = ast.getField(ifTag);
        if (f == null) return;

        String value = f.getValue().trim();

        if (ifValue.equals(value)) {
            if (ast.getField(requiredTag) == null) {
                errors.add(new ValidationError(
                        requiredTag,
                        "COND01",
                        requiredTag + " required when "
                                + ifTag + "=" + ifValue
                ));
            }
        }
    }
}