package com.github.liaomengge.base_common.sentinel.convert;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

/**
 * Created by liaomengge on 2021/1/12.
 */
@NoArgsConstructor
@AllArgsConstructor
public class DefaultConverter<T> implements Converter<String, T> {

    private static final Logger log = LyLogger.getInstance(DefaultConverter.class);

    private T defaultValue = null;

    @Override
    public T convert(String source) {
        try {
            return LyJacksonUtil.fromJson(source, new TypeReference<T>() {
            });
        } catch (Exception e) {
            log.error("convert source[" + source + "] fail", e);
            return defaultValue;
        }
    }
}
