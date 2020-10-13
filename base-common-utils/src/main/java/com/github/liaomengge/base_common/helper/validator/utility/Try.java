package com.github.liaomengge.base_common.helper.validator.utility;

import com.github.liaomengge.base_common.support.function.checked.CheckedRunnable;
import com.github.liaomengge.base_common.support.function.checked.CheckedSupplier;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An implementation of Try control.
 *
 * @param <T> Value Type in the case of success
 * @author baojie
 * @since 1.0.0
 */
public interface Try<T> {

    /**
     * Creates a Try of a CheckedSupplier.
     *
     * @param supplier A checked supplier
     * @param <T>      Component type
     * @return {@code Success(supplier.get())} if no exception occurs, otherwise {@code Failure(throwable)}
     * if an exception occurs calling {@code supplier.get()}.
     */
    static <T> Try<T> of(CheckedSupplier<? extends T> supplier) {
        try {
            return new Success<>(supplier.get());
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Creates a Try of a CheckedRunnable.
     *
     * @param runnable A checked runnable
     * @return {@code Success(null)} if no exception occurs, otherwise {@code Failure(throwable)}
     * if an exception occurs calling {@code runnable.run()}.
     */
    static Try<Void> run(CheckedRunnable runnable) {
        try {
            runnable.run();
            return new Success<>(null); // null represents the absence of an value, i.e. Void
        } catch (Throwable t) {
            return new Failure<>(t);
        }
    }

    /**
     * Creates a {@link Success} that contains the given {@code value}. Shortcut for {@code new Success<>(value)}.
     *
     * @param value A value.
     * @param <T>   Type of the given {@code value}.
     * @return A new {@code Success}.
     */
    static <T> Try<T> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a {@link Failure} that contains the given {@code exception}.
     * Shortcut for {@code new Failure<>(exception)}.
     *
     * @param exception An exception.
     * @param <T>       Component type of the {@code Try}.
     * @return A new {@code Failure}.
     */
    static <T> Try<T> failure(Throwable exception) {
        return new Failure<>(exception);
    }

    /**
     * Gets the result of this Try if this is a {@code Success} or throws if this is a {@code Failure}.
     *
     * @return The result of this Try
     * @throws RuntimeException if this is a {@code Failure}
     */
    T get();

    /**
     * Gets the cause if this is a Failure or throws if this is a {@code Success}.
     *
     * @return The cause if this is a {@code Failure}
     * @throws UnsupportedOperationException if this is a {@code Success}
     */
    Throwable getCause();

    /**
     * Checks if this is a {@code Success}.
     *
     * @return true, if this is a {@code Success}, otherwise false, if this is a {@code Failure}
     */
    boolean isSuccess();

    /**
     * Checks if this is a Failure.
     *
     * @return true, if this is a Failure, otherwise false, if this is a Success
     */
    boolean isFailure();

    /**
     * Consumes the value if this is a {@code Success}.
     *
     * @param action A value consumer
     * @return this
     * @throws NullPointerException if {@code action} is null
     */
    default Try<T> onSuccess(Consumer<? super T> action) {
        Objects.requireNonNull(action, "action is null");
        if (isSuccess()) {
            action.accept(get());
        }
        return this;
    }

    /**
     * Consumes the throwable if this is a {@code Failure}.
     *
     * @param action An exception consumer
     * @return this
     * @throws NullPointerException if {@code action} is null
     */
    default Try<T> onFailure(Consumer<? super Throwable> action) {
        Objects.requireNonNull(action, "action is null");
        if (isFailure()) {
            action.accept(getCause());
        }
        return this;
    }

    /**
     * Return the value if this is a {@code Success}, otherwise return {@code other}.
     *
     * @param other the value to be returned if this is a {@code Failure}
     * @return the value, if this is a {@code Success}, otherwise {@code other}
     */
    default T orElse(T other) {
        return isSuccess() ? get() : other;
    }

    /**
     * Return the value if this is a {@code Success}, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Supplier} whose result is returned if no value is {@code Success}
     * @return the value if this is a {@code Success} otherwise the result of {@code other.get()}
     * @throws NullPointerException if {@code other} is null
     */
    default T orElseGet(Supplier<? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        return isSuccess() ? get() : other.get();
    }

    /**
     * Return the value if this is a {@code Success}, otherwise invoke {@code other} and return
     * the result of that invocation.
     *
     * @param other a {@code Function} whose result is returned if no value is {@code Success}
     * @return the value if this is a {@code Success} otherwise the result of {@code other.apply(getCause())}
     * @throws NullPointerException if {@code other} is null
     */
    default T orElseMap(Function<? super Throwable, ? extends T> other) {
        Objects.requireNonNull(other, "other is null");
        return isFailure() ? other.apply(getCause()) : get();
    }

    /**
     * Return the value if this is a {@code Success}, otherwise invoke {@code exceptionProvider} and throw
     * the result of that invocation.
     *
     * @param exceptionProvider a {@code Function} whose result is throwed if no value is {@code Success}
     * @param <X>               The new type to throw
     * @return the value if this is a {@code Success} throw {@code exceptionProvider.apply(getCause())}
     * @throws X                    throw {@code exceptionProvider.apply(getCause())} if this is a {@code Failure}
     * @throws NullPointerException if {@code exceptionProvider} is null
     */
    default <X extends Throwable> T orElseThrow(Function<? super Throwable, X> exceptionProvider) throws X {
        Objects.requireNonNull(exceptionProvider, "exceptionProvider is null");
        if (isFailure()) {
            throw exceptionProvider.apply(getCause());
        } else {
            return get();
        }
    }

    /**
     * Runs the given checked function if this is a {@code Success},
     * passing the result of the current expression to it.
     * If this expression is a {@code Failure} then it'll return a new
     * {@code Failure} of type R with the original exception.
     *
     * @param <U>    The new component type
     * @param mapper A checked function
     * @return a {@code Try}
     * @throws NullPointerException if {@code mapper} is null
     */
    default <U> Try<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        if (isFailure()) {
            return (Failure<U>) this;
        }
        return Try.of(() -> mapper.apply(get()));
    }

    /**
     * Returns {@code this}, if this is a {@code Success},
     * otherwise tries to recover the exception of the failure with {@code f},
     * i.e. calling {@code Try.of(() -> f.apply(throwable))}.
     *
     * @param f A recovery function taking a Throwable
     * @return a {@code Try}
     * @throws NullPointerException if {@code f} is null
     */
    default Try<T> recover(Function<? super Throwable, ? extends T> f) {
        Objects.requireNonNull(f, "f is null");
        if (isFailure()) {
            return Try.of(() -> f.apply(getCause()));
        }
        return this;
    }

    /**
     * Returns {@code this}, if this is a {@code Success},
     * otherwise tries to recover the exception of the {@code Failure} with {@code f},
     * i.e. calling {@code f.apply(cause.getCause())}. If an error occurs recovering a {@code Failure},
     * then the new {@code Failure} is returned.
     *
     * @param f A recovery function taking a {@code Throwable}
     * @return a {@code Try}
     * @throws NullPointerException if {@code f} is null
     */
    default Try<T> recoverWith(Function<? super Throwable, ? extends Try<T>> f) {
        Objects.requireNonNull(f, "f is null");
        if (isFailure()) {
            try {
                return f.apply(getCause());
            } catch (Throwable t) {
                return new Failure<>(t);
            }
        } else {
            return this;
        }
    }

    /**
     * Folds either the success or the failure side of this disjunction.
     *
     * @param successMapper maps the value if this is a {@code Success}
     * @param failureMapper maps the cause if this is a {@code Failure}
     * @param <U>           type of the folded value
     * @return A value of type U
     * @throws NullPointerException if {@code successMapper} or {@code failureMapper} is null
     */
    default <U> U fold(Function<? super T, ? extends U> successMapper,
                       Function<? super Throwable, ? extends U> failureMapper) {
        Objects.requireNonNull(successMapper, "successMapper is null");
        Objects.requireNonNull(failureMapper, "failureMapper is null");
        if (isSuccess()) {
            return successMapper.apply(get());
        } else {
            return failureMapper.apply(getCause());
        }
    }

    /**
     * Applies the action to the value.
     *
     * @param action A Consumer
     * @return this {@code Try}
     * @throws NullPointerException if {@code action} is null
     */
    default Try<T> peek(Consumer<? super Try<T>> action) {
        Objects.requireNonNull(action, "action is null");
        action.accept(this);
        return this;
    }

    /**
     * Converts this {@code Try} to an {@link Either}.
     *
     * @return A new {@code Either}
     */
    default Either<Throwable, T> toEither() {
        return isSuccess() ? Either.right(get()) : Either.left(getCause());
    }

    /**
     * A succeeded Try.
     *
     * @param <T> component type of this Success
     * @author baojie
     * @since 1.0.0
     */
    final class Success<T> implements Try<T> {

        private final T value;

        /**
         * Constructs a Success.
         *
         * @param value The value of this Success.
         */
        private Success(T value) {
            this.value = value;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getCause() {
            throw new UnsupportedOperationException("getCause on Success");
        }

        @Override
        public boolean isSuccess() {
            return true;
        }

        @Override
        public boolean isFailure() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof Success && Objects.equals(value, ((Success<?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return String.format("Success(%s)", value.toString());
        }
    }

    /**
     * A failed Try.
     *
     * @param <T> component type of this Failure
     * @author baojie
     * @since 1.0.0
     */
    final class Failure<T> implements Try<T> {

        private final RuntimeException cause;

        /**
         * Constructs a Failure.
         *
         * @param exception A cause of type Throwable, may not be null.
         */
        private Failure(Throwable exception) {
            this.cause = new RuntimeException(exception);
        }

        @Override
        public T get() {
            throw cause;
        }

        @Override
        public Throwable getCause() {
            return cause.getCause();
        }

        @Override
        public boolean isSuccess() {
            return false;
        }

        @Override
        public boolean isFailure() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof Failure && Objects.equals(cause, ((Failure<?>) obj).cause));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(cause);
        }
    }
}
