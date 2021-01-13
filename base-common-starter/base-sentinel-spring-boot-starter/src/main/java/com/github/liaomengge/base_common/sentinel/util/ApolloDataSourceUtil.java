package com.github.liaomengge.base_common.sentinel.util;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.liaomengge.base_common.sentinel.consts.SentinelClusterConst;
import com.github.liaomengge.base_common.sentinel.convert.DefaultConverter;
import com.github.liaomengge.base_common.utils.json.LyJacksonUtil;
import lombok.experimental.UtilityClass;

import java.util.function.Function;

/**
 * Created by liaomengge on 2021/1/12.
 */
@UtilityClass
public class ApolloDataSourceUtil {

    public <T> ApolloDataSource<T> getInstance(String namespaceName, String ruleKey, String defaultRuleValue,
                                               Converter<String, T> parser) {
        return new ApolloDataSource<>(namespaceName, ruleKey, defaultRuleValue, parser);
    }

    public <T> ApolloDataSource<T> getInstance(String ruleKey, String defaultRuleValue, Converter<String, T> parser) {
        return getInstance(SentinelClusterConst.NAMESPACE_NAME, ruleKey, defaultRuleValue, parser);
    }

    public <T> ApolloDataSource<T> getInstance(String ruleKey, String defaultRuleValue) {
        return getInstance(ruleKey, defaultRuleValue, source -> LyJacksonUtil.fromJson(source, new TypeReference<T>() {
        }));
    }

    public <T> ApolloDataSource<T> getInstance(String ruleKey, String defaultRuleValue, T defaultValue) {
        return getInstance(ruleKey, defaultRuleValue, new DefaultConverter<>(defaultValue));
    }

    public <T> ApolloDataSource<T> getInstance(String ruleKey, Function<T, String> function, T defaultValue) {
        return getInstance(ruleKey, function.apply(defaultValue), new DefaultConverter<>(defaultValue));
    }

    public <T> ApolloDataSource<T> getInstance(String ruleKey, T defaultValue) {
        return getInstance(ruleKey, LyJacksonUtil::toJson, defaultValue);
    }
}
