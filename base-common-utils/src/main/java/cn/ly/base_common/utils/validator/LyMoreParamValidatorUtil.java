package cn.ly.base_common.utils.validator;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.*;

/**
 * Created by liaomengge on 2018/8/10.
 */
public final class LyMoreParamValidatorUtil {

    private LyMoreParamValidatorUtil() {
    }

    public static <T> ValidatorResult validate(T t, Function<T, ValidatorResult> function) {
        return Optional.ofNullable(t).map(function).orElseGet(() -> new ValidatorResult(false,
                ValidatorError.addError("value is null")));
    }

    public static <T> ValidatorResult validate(T t, Predicate<T> predicate, String errorMsg) {
        return validate(t, predicate, ValidatorError.addError(errorMsg));
    }

    public static <T> ValidatorResult validate(T t, Predicate<T> predicate, ValidatorError validatorError) {
        return validate(t, val -> predicate.test(val) ? new ValidatorResult() : new ValidatorResult(false, validatorError));
    }

    public static <T> ValidatorResult validate2(@NonNull T t, Function<T, ValidatorResult> function) {
        return Optional.ofNullable(t).map(function).orElse(null);
    }

    public static <T> ValidatorResult validate2(@NonNull T t, Predicate<T> predicate, ValidatorError validatorError) {
        return validate2(t, val -> predicate.test(val) ? new ValidatorResult() : new ValidatorResult(false, validatorError));
    }

    public static <T> ValidatorResult validate2(@NonNull T t, Predicate<T> predicate, String errorMsg) {
        return validate2(t, predicate, ValidatorError.addError(errorMsg));
    }

    public static class ValidatorResult implements Serializable {

        private static final long serialVersionUID = -4177782411877667671L;
        @Getter
        @Setter
        private boolean success;

        @Getter
        @Setter
        private ValidatorError validatorError;

        @Getter
        private List<ValidatorError> errorList = Lists.newArrayList();

        public ValidatorResult() {
            this(true);
        }

        public ValidatorResult(boolean success) {
            this.success = success;
            if (!this.success && this.validatorError != null) {
                this.errorList.add(this.validatorError);
            }
        }

        public ValidatorResult(ValidatorError validatorError) {
            this();
            this.validatorError = validatorError;
            if (!this.success && this.validatorError != null) {
                this.errorList.add(this.validatorError);
            }
        }

        public ValidatorResult(boolean success, ValidatorError validatorError) {
            this.success = success;
            this.validatorError = validatorError;
            if (!this.success && this.validatorError != null) {
                this.errorList.add(this.validatorError);
            }
        }

        public ValidatorResult and(ValidatorResult result) {
            this.success = this.isSuccess() && result.isSuccess();
            this.errorList.addAll(result.getErrorList());
            return this;
        }

        public ValidatorResult and(ValidatorResult... results) {
            for (ValidatorResult validatorResult : results) {
                this.success &= validatorResult.isSuccess();
                this.errorList.addAll(validatorResult.getErrorList());
            }
            return this;
        }

        public ValidatorResult or(ValidatorResult result) {
            this.success = this.isSuccess() || result.isSuccess();
            this.errorList.addAll(result.getErrorList());
            return this;
        }

        public ValidatorResult or(ValidatorResult... results) {
            for (ValidatorResult validatorResult : results) {
                this.success |= validatorResult.isSuccess();
                this.errorList.addAll(validatorResult.getErrorList());
            }
            return this;
        }

        @Override
        public String toString() {
            return "ValidatorResult{" +
                    "success=" + success +
                    ", errorList=" + errorList.parallelStream().map(val -> "(errorCode=" + val.getErrorCode() + ", " +
                    "errorMsg=" + val.getErrorMsg() + ", field=" + val.getField() + ", value=" + val.getInvalidValue() + ")")
                    .collect(Collectors.joining(",")) + '}';
        }
    }

    @Data
    @AllArgsConstructor
    public static class ValidatorError implements Serializable {
        private static final long serialVersionUID = -2271847138189151371L;

        private String errorCode = "";
        private String errorMsg;
        private String field;
        private Object invalidValue;

        public ValidatorError() {
        }

        public ValidatorError(String errorCode, String errorMsg) {
            this.errorCode = errorCode;
            this.errorMsg = errorMsg;
        }

        public ValidatorError setErrorCode(String errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public ValidatorError setErrorMsg(String errorMsg) {
            this.errorMsg = errorMsg;
            return this;
        }

        public static ValidatorError addError(String errorMsg) {
            return new ValidatorError().setErrorMsg(errorMsg);
        }

        public static ValidatorError addError(String errorCode, String errorMsg) {
            return new ValidatorError(errorCode, errorMsg);
        }

        public static ValidatorError addError(String errorCode, String errorMsg, String field, Object invalidValue) {
            return new ValidatorError(errorCode, errorMsg, field, invalidValue);
        }
    }
}
