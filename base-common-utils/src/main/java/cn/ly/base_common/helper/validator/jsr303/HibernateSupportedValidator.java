package cn.ly.base_common.helper.validator.jsr303;

import cn.ly.base_common.helper.validator.FunctionalValidator;
import cn.ly.base_common.helper.validator.FunctionalValidatorResult;
import cn.ly.base_common.helper.validator.bean.ValidationError;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * JSR303 Based Hibernate Validator
 *
 * @author baojie
 * @since 1.0.0
 */
public final class HibernateSupportedValidator {

    private int errorCode;

    private final Validator hibernateValidator;

    private Function<ConstraintViolation, ValidationError> transformer = v -> ValidationError.of(v.getMessage())
            .field(v.getPropertyPath().toString())
            .invalidValue(v.getInvalidValue());


    public static final Validator FAILFAST_VALIDATOR;
    public static final Validator FAILOVER_VALIDATOR;

    static {
        FAILFAST_VALIDATOR = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(true)
                .buildValidatorFactory()
                .getValidator();
        FAILOVER_VALIDATOR = Validation.byProvider(HibernateValidator.class)
                .configure()
                .failFast(false)
                .buildValidatorFactory()
                .getValidator();
    }

    /**
     * Constructs a {@code HibernateSupportedValidator}.
     *
     * @param hibernateValidator The {@code Validator}.
     */
    private HibernateSupportedValidator(Validator hibernateValidator) {
        this.hibernateValidator = hibernateValidator;
    }

    /**
     * Do default hibernate validate
     *
     * @param element The element to be checked.
     * @param <T>     Value type to be validated.
     * @return Whether the result of validator is success.
     */
    public static <T> boolean validateDefault(T element) {
        return validatorDefaultResult(element)
                .isSuccess();
    }

    /**
     * Do default hibernate validate.
     *
     * @param element element The element to be checked.
     * @param <T>     Value type to be validated.
     * @return The result of validator.
     */
    public static <T> FunctionalValidatorResult<T> validatorDefaultResult(T element) {
        return FunctionalValidator.check(element)
                .on(build().validator())
                .doValidate();
    }

    /**
     * Constructs a {@code HibernateSupportedValidator} by a {@code FAILFAST_VALIDATOR}
     *
     * @return a new {@code HibernateSupportedValidator} instance.
     */
    public static HibernateSupportedValidator build() {
        return buildByValidator(FAILFAST_VALIDATOR);
    }

    /**
     * Constructs a {@code HibernateSupportedValidator} by a {@code FAILOVER_VALIDATOR}
     *
     * @return a new {@code HibernateSupportedValidator} instance.
     */
    public static HibernateSupportedValidator buildByFailOverValidator() {
        return buildByValidator(FAILOVER_VALIDATOR);
    }

    /**
     * Constructs a {@code HibernateSupportedValidator} by a given {@code Validator}
     *
     * @param hibernateValidator A given {@code Validator}
     * @return a new {@code HibernateSupportedValidator} instance.
     * @throws NullPointerException if {@code hibernateValidator} is null.
     */
    public static HibernateSupportedValidator buildByValidator(Validator hibernateValidator) {
        Objects.requireNonNull(hibernateValidator);
        return new HibernateSupportedValidator(hibernateValidator);
    }

    /**
     * Set error code of Validator, default zero.
     *
     * @param errorCode The error code.
     * @return The instance of {@code HibernateSupportedValidator} itself.
     */
    public final HibernateSupportedValidator errorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    /**
     * Set a transformer from {@code ConstraintViolation} to {@code ValidationError}
     *
     * @param transformer A transformer from {@code ConstraintViolation} to {@code ValidationError}
     * @return The instance of {@code HibernateSupportedValidator} itself.
     * @throws NullPointerException if {@code transformer} is null.
     */
    public final HibernateSupportedValidator transformer(Function<ConstraintViolation, ValidationError> transformer) {
        this.transformer = Objects.requireNonNull(transformer);
        return this;
    }

    /**
     * Get the validator function of this {@code HibernateSupportedValidator}.
     *
     * @param <T> Value type to be validated.
     * @return The function of this {@code HibernateSupportedValidator}.
     */
    public final <T> Function<T, Stream<ValidationError>> validator() {
        return t -> hibernateValidator.validate(t).stream().map(transformer).peek(v -> v.errorCode(errorCode));
    }
}
