package com.github.liaomengge.base_common.utils.copy;

import lombok.experimental.UtilityClass;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * @author liaomengge
 * @version LyPropertyUtil.java, v 0.1 2021-11-05 11:30 liaomengge Exp $$
 */
@UtilityClass
public class LyPropertyUtil {

    public <T, E> void setProperty(T t, BiConsumer<T, E> biConsumer, Supplier<E> e) {
        if (Objects.nonNull(e)) {
            Optional.ofNullable(t).ifPresent(val -> biConsumer.accept(val, e.get()));
        }
    }
}
