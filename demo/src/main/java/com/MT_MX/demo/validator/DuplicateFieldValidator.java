package com.MT_MX.demo.validator;
import com.MT_MX.demo.Exception.ValidationError;
import com.MT_MX.demo.ast.BlockNode;
import com.MT_MX.demo.ast.FieldNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class DuplicateFieldValidator {

    private DuplicateFieldValidator() {}
    private static final List<String> ALLOWED_DUPLICATES =
            List.of("72","70");

    public static void checkNoDuplicates(
            BlockNode block,
            List<ValidationError> errors
    ) {

        Map<String, Integer> counts = new HashMap<>();

        for (FieldNode field : block.getFields()) {
            counts.merge(field.getTag(), 1, Integer::sum);
        }

        counts.forEach((tag, count) -> {
            if (count > 1 && !ALLOWED_DUPLICATES.contains(tag)) {
                errors.add(new ValidationError(
                        tag,
                        "DUP01",
                        "Duplicate field detected (" + count + " occurrences)"
                ));
            }
        });
    }
}