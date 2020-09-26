package cn.ly.base_common.support.optional;

import java.util.Optional;
import java.util.function.Supplier;

import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2019/11/13.
 */
@UtilityClass
public class _Optionals {

    public <T> T get(T t) {
        return Optional.ofNullable(t).orElse(null);
    }

    public <T> T get(T t, T val) {
        return Optional.ofNullable(t).orElse(val);
    }

    public <T> T get(T t, Supplier<? extends T> supplier) {
        return Optional.ofNullable(t).orElseGet(supplier);
    }
}
