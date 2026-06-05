package com.MT_MX.demo.rule;

import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.FieldNode;
import com.MT_MX.demo.ast.SwiftAst;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
public class PatternRule implements ConditionalRule{
    private final String tag;
    private final Pattern pattern;

    public PatternRule(String tag, String regex) {
        this.tag = tag;
        this.pattern = Pattern.compile(regex);
    }

    @Override
    public void apply(SwiftAst ast, List<ValidationError> errors) {

        FieldNode f = ast.getField(tag);
        if (f == null) return;

        String value = f.getValue().trim();

        if (!pattern.matcher(value).matches()) {
            errors.add(new ValidationError(
                    tag,
                    "PAT01",
                    "Invalid format for :" + tag
            ));
        }
    }
}
