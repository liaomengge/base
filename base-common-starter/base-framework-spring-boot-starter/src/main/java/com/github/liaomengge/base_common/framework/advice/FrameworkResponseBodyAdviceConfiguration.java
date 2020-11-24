package com.github.liaomengge.base_common.framework.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.framework.FrameworkProperties.ResponseBodyAdviceProperties;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/11/23.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
public class FrameworkResponseBodyAdviceConfiguration {

    private final ObjectMapper objectMapper;
    private final FrameworkProperties frameworkProperties;

    @Bean("com.github.liaomengge.base_common.framework.advice.FrameworkResponseBodyAdvice")
    @ConditionalOnMissingBean
    public FrameworkResponseBodyAdvice frameworkResponseBodyAdvice() {
        ResponseBodyAdviceProperties adviceProperties = frameworkProperties.getResponseBodyAdvice();
        return new FrameworkResponseBodyAdvice(adviceProperties.isEnabled(), adviceProperties.getIgnoreBasePackages(),
                objectMapper);
    }
}
