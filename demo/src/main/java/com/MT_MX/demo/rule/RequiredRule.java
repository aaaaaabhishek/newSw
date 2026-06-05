package com.MT_MX.demo.rule;
import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.SwiftAst;
import org.springframework.stereotype.Component;

import java.util.List;
public class RequiredRule implements ConditionalRule{
        private final String tag;
       public RequiredRule(String tag) {
            this.tag = tag;
        }

        @Override
        public void apply(SwiftAst ast, List<ValidationError> errors) {

            if (ast.getField(tag) == null) {
                errors.add(new ValidationError(
                        tag,
                        "REQ01",
                        "Missing mandatory field :" + tag
                ));
            }
        }
    }