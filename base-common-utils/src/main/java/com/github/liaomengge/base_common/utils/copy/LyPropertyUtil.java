package com.github.liaomengge.base_common.utils.copy;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2021/11/05
 */
@UtilityClass
public class LyPropertyUtil {

    public <T, E> void setProperty(T t, BiConsumer<T, E> biConsumer, Supplier<E> e) {
        if (Objects.nonNull(e)) {
            Optional.ofNullable(t).ifPresent(val -> biConsumer.accept(val, e.get()));
        }
    }
}
