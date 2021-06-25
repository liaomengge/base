package com.github.liaomengge.base_common.sentinel.convert;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by liaomengge on 2021/1/12.
 */
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class DefaultConverter<T> implements Converter<String, T> {
    
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
