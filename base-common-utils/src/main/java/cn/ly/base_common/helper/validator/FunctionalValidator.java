package cn.ly.base_common.helper.validator;

import cn.ly.base_common.helper.validator.bean.ValidationError;
import cn.ly.base_common.helper.validator.bean.ValidationResult;
import cn.ly.base_common.helper.validator.utility.Try;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * A functional Chained call validator
 * 参照：https://github.com/javadeep/common-functional
 *
 * @param <T> Value type to be validated.
 * @author baojie
 * @since 1.0.0
 */
public final class FunctionalValidator<T> {

    private final T element;
    private boolean isFailFast = true;
    private final List<Function<? super T, Stream<ValidationError>>> validators = new LinkedList<>();

    /**
     * Constructs a {@code FunctionalValidator}.
     *
     * @param element The element to be checked.
     */
    private FunctionalValidator(T element) {
        this.element = element;
    }

    /**
     * Constructs a {@code FunctionalValidator} from an element.
     *
     * @param element The element to be checked.
     * @param <T>     The type of element to be checked.
     * @return a new {@code FunctionalValidator} instance.
     */
    public static <T> FunctionalValidator<T> check(T element) {
        return new FunctionalValidator<>(element);
    }

    /**
     * The method to prevent the following validators from getting validated if any validator fails.
     *
     * @return the instance of {@code FunctionalValidator} itself.
     */
    public final FunctionalValidator<T> failFast() {
        isFailFast = true;
        return this;
    }

    /**
     * The method to ignore the failures so that all the validators will work in order.
     *
     * @return the instance of {@code FunctionalValidator} itself.
     */
    public final FunctionalValidator<T> failOver() {
        isFailFast = false;
        return this;
    }

    /**
     * Add a validator by a {@code Function}
     *
     * @param v The validator {@code Function}
     * @return The instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code v} is null.
     */
    public final FunctionalValidator<T> on(Function<? super T, Stream<ValidationError>> v) {
        Objects.requireNonNull(v, "v is null");
        validators.add(v);
        return this;
    }

    /**
     * Add a validator by a {@code validatorPredicate} and an {@code errorMsg}
     *
     * @param validatorPredicate a {@code Predicate} validator
     * @param errorMsg           The error message
     * @return The instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code errorMsg} is null.
     */
    public final FunctionalValidator<T> on(Predicate<? super T> validatorPredicate, String errorMsg) {
        Objects.requireNonNull(errorMsg, "errorMsg is null");
        return on(validatorPredicate, ValidationError.of(errorMsg));
    }

    /**
     * Add a validator by a {@code validatorPredicate} and a {@code ValidationError}
     *
     * @param validatorPredicate a {@code Predicate} validator
     * @param error              The validator error
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code error} is null.
     */
    public final FunctionalValidator<T> on(Predicate<? super T> validatorPredicate, ValidationError error) {
        Objects.requireNonNull(error, "error is null");
        return on(validatorPredicate, Stream.of(error));
    }

    /**
     * Add a validator by a {@code validatorPredicate} and a {@code ValidationError} list
     *
     * @param validatorPredicate a {@code Predicate} validator
     * @param errors             The validator error list
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code errors} is null.
     */
    public final FunctionalValidator<T> on(Predicate<? super T> validatorPredicate, Stream<ValidationError> errors) {
        Objects.requireNonNull(validatorPredicate, "validatorPredicate is null");
        Objects.requireNonNull(errors, "errors is null");
        return on(t -> validatorPredicate.test(t) ? Stream.empty() : errors);
    }

    /**
     * Add a validator by a {@code Function} and
     * a {@code conditionPredicate} to determine whether to do validation the target or not.
     *
     * @param v                  The validator {@code Function}
     * @param conditionPredicate The condition predicate to determine whether to do validation the target or not.
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code v} or {@code conditionPredicate} is null.
     */
    public final FunctionalValidator<T> onIf(Function<? super T, Stream<ValidationError>> v,
                                             Predicate<? super T> conditionPredicate) {

        Objects.requireNonNull(v, "v is null");
        Objects.requireNonNull(conditionPredicate, "conditionPredicate is null");
        validators.add(t -> conditionPredicate.test(t) ? v.apply(t) : Stream.empty());
        return this;
    }

    /**
     * Add a validator by a {@code validatorPredicate} and an {@code errorMsg} and
     * a {@code conditionPredicate} to determine whether to do validation the target or not.
     *
     * @param validatorPredicate a {@code Predicate} validator.
     * @param errorMsg           The error message.
     * @param conditionPredicate The condition predicate to determine whether to do validation the target or not.
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code errorMsg}
     *                              or {@code conditionPredicate} is null.
     */
    public final FunctionalValidator<T> onIf(Predicate<? super T> validatorPredicate, String errorMsg,
                                             Predicate<? super T> conditionPredicate) {
        Objects.requireNonNull(errorMsg, "errorMsg is null");
        return onIf(validatorPredicate, ValidationError.of(errorMsg), conditionPredicate);
    }

    /**
     * Add a validator by a {@code validatorPredicate} and a {@code ValidationError} and
     * a {@code conditionPredicate} to determine whether to do validation the target or not.
     *
     * @param validatorPredicate a {@code Predicate} validator.
     * @param error              The validator error.
     * @param conditionPredicate The condition predicate to determine whether to do validation the target or not.
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code error}
     *                              or {@code conditionPredicate} is null.
     */
    public final FunctionalValidator<T> onIf(Predicate<? super T> validatorPredicate, ValidationError error,
                                             Predicate<? super T> conditionPredicate) {
        Objects.requireNonNull(error, "error is null");

        return onIf(validatorPredicate, Stream.of(error), conditionPredicate);
    }

    /**
     * Add a validator by a {@code validatorPredicate} and a {@code ValidationError} list and
     * a {@code conditionPredicate} to determine whether to do validation the target or not.
     *
     * @param validatorPredicate a {@code Predicate} validator.
     * @param errors             The validator error list.
     * @param conditionPredicate The condition predicate to determine whether to do validation the target or not.
     * @return the instance of {@code FunctionalValidator} itself.
     * @throws NullPointerException if {@code validatorPredicate} or {@code errors}
     *                              or {@code conditionPredicate} is null.
     */
    public final FunctionalValidator<T> onIf(Predicate<? super T> validatorPredicate, Stream<ValidationError> errors,
                                             Predicate<? super T> conditionPredicate) {
        Objects.requireNonNull(validatorPredicate, "validatorPredicate is null");
        Objects.requireNonNull(errors, "errors is null");
        Objects.requireNonNull(conditionPredicate, "conditionPredicate is null");

        return onIf(t -> validatorPredicate.test(t) ? Stream.empty() : errors, conditionPredicate);
    }

    /**
     * Execute validation.
     *
     * @return The result of validation.
     */
    public final FunctionalValidatorResult<T> doValidate() {

        long startTime = System.currentTimeMillis();
        ValidationResult result = ValidationResult.build();

        try {
            if (isFailFast) {
                for (Function<? super T, Stream<ValidationError>> validator : validators) {
                    result.addErrors(validator.apply(element));
                    if (!result.isSuccess()) {
                        break;
                    }
                }
            } else {
                validators.forEach(v -> result.addErrors(v.apply(element)));
            }
            return FunctionalValidatorResult.of(element,
                    Try.of(() -> result.timeElapsed(System.currentTimeMillis() - startTime)));
        } catch (Throwable e) {
            return FunctionalValidatorResult.of(element, Try.failure(e));
        }
    }
}
