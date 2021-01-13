package com.github.liaomengge.base_common.sentinel.util;

import com.github.liaomengge.base_common.sentinel.convert.DefaultConverter;
import lombok.experimental.UtilityClass;

/**
 * Created by liaomengge on 2021/1/12.
 */
@UtilityClass
public class ConverterUtil {

    public <T> DefaultConverter<T> getInstance() {
        return new DefaultConverter<>();
    }

    public <T> DefaultConverter<T> getInstance(T defaultValue) {
        return new DefaultConverter<>(defaultValue);
    }

    public <T> T convert(String source) {
        return (T) getInstance().convert(source);
    }

    public <T> T convert(T defaultValue, String source) {
        return getInstance(defaultValue).convert(source);
    }
}
