package com.github.liaomengge.base_common.metric.cache.local;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.liaomengge.base_common.cache.caffeine.CaffeineCache;
import com.github.liaomengge.base_common.cache.caffeine.CaffeineCacheManager;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/22.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(MetricsAutoConfiguration.class)
@ConditionalOnClass({MeterRegistry.class, CaffeineCacheManager.class, CaffeineCache.class, Cache.class})
@ConditionalOnProperty(prefix = "base.metric.cache.local", name = "enabled", matchIfMissing = true)
public class LocalCacheMeterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public LocalCacheMeterBinder localCacheMeterBinder() {
        return new LocalCacheMeterBinder();
    }
}
