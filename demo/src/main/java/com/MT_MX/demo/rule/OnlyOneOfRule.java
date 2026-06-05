package com.MT_MX.demo.rule;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import org.springframework.stereotype.Component;

import java.util.List;
public class OnlyOneOfRule implements ConditionalRule{
        private final List<String> tags;

    public OnlyOneOfRule(List<String> tags) {
            this.tags = tags;
        }

        @Override
        public void apply(SwiftAst ast, List<ValidationError> errors) {

            int count = 0;
            String foundTag = null;

            for (String tag : tags) {
                if (ast.getField(tag) != null) {
                    count++;
                    foundTag = tag;
                }
            }

            if (count > 1) {
                errors.add(new ValidationError(
                        foundTag,
                        "STR01",
                        "Only one of " + tags + " is allowed"
                ));
            }
        }
    }