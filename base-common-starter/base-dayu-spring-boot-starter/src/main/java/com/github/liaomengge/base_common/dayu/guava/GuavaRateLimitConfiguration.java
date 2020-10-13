package com.github.liaomengge.base_common.dayu.guava;

import com.github.liaomengge.base_common.dayu.guava.callback.UrlRateLimitHandler;
import com.github.liaomengge.base_common.dayu.guava.callback.WebCallbackManager;
import com.github.liaomengge.base_common.dayu.guava.interceptor.GuavaRateLimitHandlerInterceptor;
import com.github.liaomengge.base_common.dayu.guava.interceptor.GuavaRateLimitWebMvcConfigurer;
import com.github.liaomengge.base_common.dayu.guava.reload.GuavaRateLimitReloadListener;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.annotation.PostConstruct;

import static com.github.liaomengge.base_common.dayu.guava.consts.GuavaRateLimitConst.GUAVA_RATE_LIMIT_PREFIX;

/**
 * Created by liaomengge on 2019/8/12.
 */
@Configuration
@ConditionalOnProperty(prefix = GUAVA_RATE_LIMIT_PREFIX, name = "enabled", havingValue = "true")
@EnableConfigurationProperties(GuavaRateLimitProperties.class)
@Import(GuavaRateLimitWebMvcConfigurer.class)
public class GuavaRateLimitConfiguration {

    @Autowired(required = false)
    private UrlRateLimitHandler urlRateLimitHandler;

    private GuavaRateLimitProperties guavaRateLimitProperties;

    public GuavaRateLimitConfiguration(GuavaRateLimitProperties guavaRateLimitProperties) {
        this.guavaRateLimitProperties = guavaRateLimitProperties;
    }

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    public GuavaRateLimitHandlerInterceptor guavaRateLimitHandlerInterceptor(MeterRegistry meterRegistry) {
        return new GuavaRateLimitHandlerInterceptor(meterRegistry, guavaRateLimitProperties);
    }

    @Bean
    public GuavaRateLimitReloadListener guavaRateLimitReloadListener() {
        return new GuavaRateLimitReloadListener();
    }

    @PostConstruct
    private void init() {
        if (urlRateLimitHandler != null) {
            WebCallbackManager.setUrlBlockHandler(urlRateLimitHandler);
        }
    }
}
