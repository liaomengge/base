package com.github.liaomengge.base_common.utils.copy;

import lombok.experimental.UtilityClass;
import org.springframework.boot.context.properties.PropertyMapper;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by liaomengge on 2020/10/22.
 */
@UtilityClass
public class LyPropertyMapperUtil {

    public PropertyMapper get() {
        return PropertyMapper.get();
    }

    public PropertyMapper get(PropertyMapper.SourceOperator operator) {
        return get().alwaysApplying(operator);
    }

    public PropertyMapper getNonNull() {
        return get().alwaysApplyingWhenNonNull();
    }

    public <T> void copyProperty(Supplier<T> sourceSupplier, Consumer<T> targetConsumer) {
        getNonNull().from(sourceSupplier).to(targetConsumer);
    }

    public <T, R> void copyProperty(Supplier<T> sourceSupplier, Function<T, R> function, Consumer<R> targetConsumer) {
        getNonNull().from(sourceSupplier).as(function).to(targetConsumer);
    }
}
