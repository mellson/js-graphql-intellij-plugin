package com.intellij.lang.jsgraphql.types.schema.validation;

import com.intellij.lang.jsgraphql.types.Internal;

import java.util.LinkedHashSet;
import java.util.Set;

@Internal
public class SchemaValidationErrorCollector {

    private final LinkedHashSet<SchemaValidationError> errors = new LinkedHashSet<>();

    public void addError(SchemaValidationError validationError) {
        this.errors.add(validationError);
    }

    public Set<SchemaValidationError> getErrors() {
        return errors;
    }

    public boolean containsValidationError(SchemaValidationErrorType validationErrorType) {
        for (SchemaValidationError validationError : errors) {
            if (validationError.getErrorType() == validationErrorType) return true;
        }
        return false;
    }
}
