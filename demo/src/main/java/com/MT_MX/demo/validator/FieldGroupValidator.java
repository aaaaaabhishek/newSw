package com.MT_MX.demo.validator;
import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.BlockNode;

import java.util.List;

public final class FieldGroupValidator {

    private FieldGroupValidator() {}

    public static void requireOneOf(
            BlockNode block,
            List<String> tags,
            String groupName,
            List<ValidationError> errors
    ) {

        boolean found = tags.stream()
                .anyMatch(tag -> block.getField(tag) != null);

        if (!found) {
            errors.add(new ValidationError(
                    groupName,
                    "GRP01",
                    "One of fields required: " + tags
            ));
        }
    }
}
