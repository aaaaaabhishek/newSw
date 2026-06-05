package com.MT_MX.demo.rule;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import org.springframework.stereotype.Component;

import java.util.List;
public class PresentThenRequiredRule implements ConditionalRule{
    private final String ifTag;
    private final String requiredTag;

    public PresentThenRequiredRule(String ifTag, String requiredTag) {
        this.ifTag = ifTag;
        this.requiredTag = requiredTag;
    }

    @Override
    public void apply(SwiftAst ast, List<ValidationError> errors) {

        FieldNode f = ast.getField(ifTag);
        if (f == null) return;

        if (ast.getField(requiredTag) == null) {
            errors.add(new ValidationError(
                    requiredTag,
                    "COND02",
                    requiredTag + " required when " + ifTag + " is present"
            ));
        }
    }
}
