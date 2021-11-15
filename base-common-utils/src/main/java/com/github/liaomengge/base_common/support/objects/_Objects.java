package com.github.liaomengge.base_common.support.objects;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2019/11/27.
 */
@UtilityClass
public class _Objects {

    public <T> T defaultIfNull(T t, T defaultValue) {
        return Objects.isNull(t) ? defaultValue : t;
    }

    public <T, R> R defaultIfNull(T t, Function<T, R> function) {
        return Optional.ofNullable(t).map(function).orElse(null);
    }

    public <T, R> R defaultIfNull(T t, Function<T, R> function, R defaultValue) {
        return Optional.ofNullable(t).map(function).orElse(defaultValue);
    }

    public <T, R> R defaultIfNull(T t, Function<T, R> function, Supplier<R> supplier) {
        return Optional.ofNullable(t).map(function).orElseGet(supplier);
    }
}
