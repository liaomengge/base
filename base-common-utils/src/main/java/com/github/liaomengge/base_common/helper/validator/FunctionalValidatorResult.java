package com.github.liaomengge.base_common.helper.validator;

import com.github.liaomengge.base_common.helper.validator.utility.Try;
import com.github.liaomengge.base_common.helper.validator.bean.ValidationResult;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * The Validator Result
 *
 * @param <T> Value type to be validated.
 * @author baojie
 * @since 1.0.0
 */
public final class FunctionalValidatorResult<T> {

    private final T element;
    private final Try<ValidationResult> result;

    /**
     * Constructs a {@code FunctionalValidatorResult}.
     *
     * @param element The element to be checked.
     * @param result  The result of validation.
     */
    private FunctionalValidatorResult(T element, Try<ValidationResult> result) {
        this.element = element;
        this.result = result;
    }

    /**
     * Constructs a {@code FunctionalValidatorResult}.
     *
     * @param element The element to be checked.
     * @param result  The result of validation.
     * @param <T>     The type of element to be checked
     * @return The instatnce of {@code FunctionalValidatorResult}.
     * @throws NullPointerException if {@code result} is null.
     */
    public static <T> FunctionalValidatorResult<T> of(T element, Try<ValidationResult> result) {
        return new FunctionalValidatorResult<>(element, Objects.requireNonNull(result, "result is null"));
    }

    /**
     * Consumes the result of validation.
     *
     * @param action A {@code Consumer}.
     * @return this.
     * @throws NullPointerException if {@code action} is null.
     */
    public final FunctionalValidatorResult<T> onResult(Consumer<ValidationResult> action) {
        result.onSuccess(action);
        return this;
    }

    /**
     * Consume the {@code element} if validation is success.
     *
     * @param action A {@code Consumer}.
     * @return this.
     * @throws NullPointerException if {@code action} is null.
     */
    public final FunctionalValidatorResult<T> onSuccess(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(element);
        }
        return this;
    }

    /**
     * Consume the validation result if validation is failure.
     *
     * @param action A {@code Consumer}.
     * @return this.
     * @throws NullPointerException if {@code action} is null.
     */
    public final FunctionalValidatorResult<T> onFailure(Consumer<ValidationResult> action) {
        Objects.requireNonNull(action, "action is null");
        if (result.isSuccess() && !result.get().isSuccess()) {
            action.accept(result.get());
        }
        return this;
    }

    /**
     * Consume the throwable if validation throws {@code Throwable}.
     *
     * @param action A {@code Consumer}.
     * @return this.
     * @throws NullPointerException if {@code action} is null.
     */
    public final FunctionalValidatorResult<T> onThrowable(Consumer<? super Throwable> action) {
        result.onFailure(action);
        return this;
    }

    /**
     * Checks whether the result of validator is success.
     *
     * @return true, if result is success, otherwise false, if result is failure of throws {@code Throwable}.
     */
    public final boolean isSuccess() {
        return result.isSuccess() && result.get().isSuccess();
    }

    /**
     * Folds every side of validation result.
     *
     * @param successMapper   maps the value if validation result is success.
     * @param failureMapper   maps the {@code ValidationResult} if validation result is failure.
     * @param throwableMapper maps the {@code Throwable} if validation throws {@code Throwable}.
     * @param <U>             type of the folded value.
     * @return A value of type U.
     * @throws NullPointerException if {@code successMapper} or {@code failureMapper}
     *                              or {@code throwableMapper} is null
     */
    public final <U> U fold(Function<? super T, ? extends U> successMapper,
                            Function<? super ValidationResult, ? extends U> failureMapper,
                            Function<? super Throwable, ? extends U> throwableMapper) {
        Objects.requireNonNull(successMapper, "successMapper is null");
        Objects.requireNonNull(failureMapper, "failureMapper is null");
        return result.fold(res -> res.isSuccess() ? successMapper.apply(element) : failureMapper.apply(res),
                throwableMapper);
    }

    /**
     * Folds either the success or the failure side of validation result and ignore {@code Throwable}
     *
     * @param successMapper maps the value if validation result is success.
     * @param failureMapper maps the {@code ValidationResult} if validation result is failure.
     * @param <U>           type of the folded value.
     * @return A value of type U.
     * @throws NullPointerException if {@code successMapper} or {@code failureMapper} is null.
     * @throws RuntimeException     if validation throws {@code Throwable}
     */
    public final <U> U foldIgnoreThrowable(Function<? super T, ? extends U> successMapper,
                                           Function<? super ValidationResult, ? extends U> failureMapper) {
        Objects.requireNonNull(successMapper, "successMapper is null");
        Objects.requireNonNull(failureMapper, "failureMapper is null");
        ValidationResult res = result.get();
        return res.isSuccess() ? successMapper.apply(element) : failureMapper.apply(res);
    }

    public Try<ValidationResult> getResult() {
        return result;
    }

    public ValidationResult flatResult() {
        if (result.isSuccess()) {
            return Optional.ofNullable(result.get()).orElseGet(ValidationResult::build);
        }
        return ValidationResult.build(false);
    }
}
