package com.github.liaomengge.base_common.helper.validator.exception;

import com.github.liaomengge.base_common.helper.validator.bean.ValidationResult;

import java.util.Objects;

/**
 * Validation Exception
 *
 * @author baojie
 * @since 1.0.0
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = -6435244278336681010L;
    
    private final ValidationResult result;

    /**
     * Construct the {@code ValidationExcepiton} instance.
     *
     * @param result The {@code ValidationResult}
     * @throws NullPointerException if {@code result} is null
     */
    public ValidationException(ValidationResult result) {
        super(Objects.requireNonNull(result, "result is null").getGlobalErrorMessage());
        this.result = result;
    }

    public ValidationResult getResult() {
        return result;
    }
}
