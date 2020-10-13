package com.github.liaomengge.base_common.helper.validator.utility;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Either represents a value of two possible types. An Either is either a {@link Left} or a
 * {@link Right}.
 *
 * @param <L> The type of the Left value of an Either.
 * @param <R> The type of the Right value of an Either.
 * @author baojie
 * @since 1.0.0
 */
public interface Either<L, R> {

    /**
     * Constructs a {@link Right}
     *
     * @param right The value.
     * @param <L>   Type of left value.
     * @param <R>   Type of right value.
     * @return A new {@code Right} instance.
     */
    static <L, R> Either<L, R> right(R right) {
        return new Right<>(right);
    }

    /**
     * Constructs a {@link Left}
     *
     * @param left The value.
     * @param <L>  Type of left value.
     * @param <R>  Type of right value.
     * @return A new {@code Left} instance.
     */
    static <L, R> Either<L, R> left(L left) {
        return new Left<>(left);
    }

    /**
     * Returns the left value.
     *
     * @return The left value.
     * @throws NoSuchElementException if this is a {@code Right}.
     */
    L getLeft();

    /**
     * Returns the right value.
     *
     * @return The right value.
     * @throws NoSuchElementException if this is a {@code Right}.
     */
    R getRight();


    /**
     * Returns whether this Either is a Left.
     *
     * @return true, if this is a Left, false otherwise
     */
    boolean isLeft();

    /**
     * Returns whether this Either is a Right.
     *
     * @return true, if this is a Right, false otherwise
     */
    boolean isRight();

    /**
     * Gets the Right value or an alternate value, if the projected Either is a Left.
     *
     * @param other the value to be returned if this is a {@code Left}
     * @return the right value, if the underlying Either is a Right or else the alternative value.
     */
    default R orElse(R other) {
        return isRight() ? getRight() : other;
    }

    /**
     * Gets the Right value or an alternate value, if the projected Either is a Left.
     *
     * @param other a {@code Supplier} which provides an alternative Right value
     * @return the right value, if the underlying Either is a Right or else the alternative Right value provided by
     * {@code other} by applying the Left value.
     * @throws NullPointerException if {@code other} is null
     */
    default R orElseGet(Supplier<? extends R> other) {
        Objects.requireNonNull(other, "other is null");
        return isRight() ? getRight() : other.get();
    }

    /**
     * Gets the Right value or an alternate value, if the projected Either is a Left.
     *
     * @param other a {@code Function} which converts a Left value to an alternative Right value
     * @return the right value, if the underlying Either is a Right or else the alternative Right value provided by
     * {@code other} by applying the Left value.
     * @throws NullPointerException if {@code other} is null
     */
    default R orElseMap(Function<? super L, ? extends R> other) {
        Objects.requireNonNull(other, "other is null");
        return isRight() ? getRight() : other.apply(getLeft());
    }

    /**
     * Runs an action in the case this is a projection on a Left value.
     *
     * @param action an action which consumes a Left value
     * @return this {@code Either}
     * @throws NullPointerException if {@code action} is null
     */
    default Either<L, R> orElseRun(Consumer<? super L> action) {
        Objects.requireNonNull(action, "action is null");
        if (isLeft()) {
            action.accept(getLeft());
        }
        return this;
    }

    /**
     * Gets the Right value or throws, if the projected Either is a Left.
     *
     * @param <X>               a throwable type
     * @param exceptionFunction a function which creates an exception based on a Left value
     * @return the right value, if the underlying Either is a Right or else throws the exception provided by
     * {@code exceptionFunction} by applying the Left value.
     * @throws X if the projected Either is a Left
     */
    default <X extends Throwable> R orElseThrow(Function<? super L, X> exceptionFunction) throws X {
        Objects.requireNonNull(exceptionFunction, "exceptionFunction is null");
        if (isRight()) {
            return getRight();
        } else {
            throw exceptionFunction.apply(getLeft());
        }
    }

    /**
     * Maps either the left or the right side of this disjunction.
     *
     * @param leftMapper  maps the left value if this is a Left
     * @param rightMapper maps the right value if this is a Right
     * @param <X>         The new left type of the resulting Either
     * @param <Y>         The new right type of the resulting Either
     * @return A new Either instance
     * @throws NullPointerException if {@code leftMapper} or {@code rightMapper} is null
     */
    default <X, Y> Either<X, Y> map(Function<? super L, ? extends X> leftMapper,
                                    Function<? super R, ? extends Y> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        if (isRight()) {
            return new Right<>(rightMapper.apply(getRight()));
        } else {
            return new Left<>(leftMapper.apply(getLeft()));
        }
    }

    /**
     * Maps the value of this Either if it is a {@link Right}, performs no operation if this is a {@link Left}.
     *
     * @param mapper A mapper
     * @param <U>    Component type of the mapped right value
     * @return a mapper
     * @throws NullPointerException if {@code mapper} is null
     */
    default <U> Either<L, U> mapRight(Function<? super R, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isRight() ? new Right<>(mapper.apply(getRight())) : (Either<L, U>) this;
    }

    /**
     * Maps the value of this Either if it is a {@link Left}, performs no operation if this is a {@link Right}.
     *
     * @param mapper A mapper
     * @param <U>    Component type of the mapped left value
     * @return a mapper
     * @throws NullPointerException if {@code mapper} is null
     */
    default <U> Either<U, R> mapLeft(Function<? super L, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "mapper is null");
        return isLeft() ? new Left<>(mapper.apply(getLeft())) : (Either<U, R>) this;
    }

    /**
     * Folds either the left or the right side of this disjunction.
     *
     * @param leftMapper  maps the left value if this is a Left
     * @param rightMapper maps the right value if this is a Right
     * @param <U>         type of the folded value
     * @return A value of type U
     * @throws NullPointerException if {@code leftMapper} or {@code rightMapper} is null
     */
    default <U> U fold(Function<? super L, ? extends U> leftMapper, Function<? super R, ? extends U> rightMapper) {
        Objects.requireNonNull(leftMapper, "leftMapper is null");
        Objects.requireNonNull(rightMapper, "rightMapper is null");
        if (isRight()) {
            return rightMapper.apply(getRight());
        } else {
            return leftMapper.apply(getLeft());
        }
    }

    /**
     * Applies the action to the value.
     *
     * @param action A Consumer
     * @return this {@code Either}
     * @throws NullPointerException if {@code action} is null
     */
    default Either<L, R> peek(Consumer<? super Either<L, R>> action) {
        Objects.requireNonNull(action, "action is null");
        action.accept(this);
        return this;
    }

    /**
     * The {@code Right} version of an {@code Either}.
     *
     * @param <L> left component type
     * @param <R> right component type
     * @author baojie
     * @since 1.0.0
     */
    final class Right<L, R> implements Either<L, R> {

        private final R value;

        /**
         * Constructs a {@code Right}.
         *
         * @param value a right value
         */
        private Right(R value) {
            this.value = value;
        }

        @Override
        public L getLeft() {
            throw new NoSuchElementException("getLeft() on Right");
        }

        @Override
        public R getRight() {
            return value;
        }

        @Override
        public boolean isLeft() {
            return false;
        }

        @Override
        public boolean isRight() {
            return true;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof Right && Objects.equals(value, ((Right<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return String.format("Right(%s)", value.toString());
        }
    }

    /**
     * The {@code Left} version of an {@code Either}.
     *
     * @param <L> left component type
     * @param <R> right component type
     * @author baojie
     * @since 1.0.0
     */
    final class Left<L, R> implements Either<L, R> {

        private final L value;

        /**
         * Constructs a {@code Left}.
         *
         * @param value a left value
         */
        private Left(L value) {
            this.value = value;
        }

        @Override
        public L getLeft() {
            return value;
        }

        @Override
        public R getRight() {
            throw new NoSuchElementException("getRight() on Left");
        }

        @Override
        public boolean isLeft() {
            return true;
        }

        @Override
        public boolean isRight() {
            return false;
        }

        @Override
        public boolean equals(Object obj) {
            return obj == this
                    || (obj instanceof Left && Objects.equals(value, ((Left<?, ?>) obj).value));
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(value);
        }

        @Override
        public String toString() {
            return String.format("Left(%s)", value.toString());
        }
    }
}
