package com.github.liaomengge.base_common.support.optional;

import lombok.experimental.UtilityClass;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2019/11/13.
 */
@UtilityClass
public class _Optionals {

    public <T> Optional<T> of(T t) {
        return Optional.ofNullable(t);
    }

    public <T> T get(T t) {
        return Optional.ofNullable(t).orElse(null);
    }

    public <T> T get(T t, T val) {
        return Optional.ofNullable(t).orElse(val);
    }

    public <T> T get(T t, Supplier<? extends T> supplier) {
        return Optional.ofNullable(t).orElseGet(supplier);
    }

    public <T, R> R convert(T t, Function<T, R> function) {
        return convertOptional(t, function).orElse(null);
    }

    public <T, R> Optional<R> convertOptional(T t, Function<T, R> function) {
        return Optional.ofNullable(t).map(function);
    }

    public <T, R, U> U convert(T t, Function<T, R> trFunction, Function<R, U> ruFunction) {
        return convert(t, trFunction, ruFunction, null);
    }

    public <T, R, U> U convert(T t, Function<T, R> trFunction, Function<R, U> ruFunction, U u) {
        return convertOptional(t, trFunction, ruFunction).orElse(u);
    }

    public <T, R, U> Optional<U> convertOptional(T t, Function<T, R> trFunction, Function<R, U> ruFunction) {
        return Optional.ofNullable(t).map(trFunction).map(ruFunction);
    }
}
