package com.github.liaomengge.base_common.dayu.sentinel.filter;

import com.github.liaomengge.base_common.dayu.sentinel.SentinelProperties;
import com.google.common.collect.ImmutableMap;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by liaomengge on 2019/11/7.
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
public class SentinelWebConfiguration {

    @Autowired
    private SentinelProperties sentinelProperties;

    @Bean("sentinelFilter")
    public SentinelFilter sentinelFilter(MeterRegistry meterRegistry) {
        return new SentinelFilter(meterRegistry);
    }

    @Bean("sentinelFilterRegistrationBean")
    @ConditionalOnMissingBean(name = "sentinelFilterRegistrationBean")
    public FilterRegistrationBean sentinelFilterRegistrationBean(SentinelFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();

        SentinelProperties.FilterProperties filterProperties = sentinelProperties.getFilter();
        String[] patterns = {"/*"};
        List<String> urlPatterns = filterProperties.getUrlPatterns();
        if (CollectionUtils.isNotEmpty(urlPatterns)) {
            patterns = urlPatterns.stream().toArray(String[]::new);
        }
        registration.addUrlPatterns(patterns);
        if (StringUtils.isNotBlank(filterProperties.getExcludedUris())) {
            registration.setInitParameters(ImmutableMap.of("excludedUris", filterProperties.getExcludedUris()));
        }
        registration.setFilter(filter);
        registration.setName("sentinelFilterRegistrationBean");
        registration.setOrder(filterProperties.getOrder());
        log.info("[Sentinel Starter] register Sentinel with urlPatterns: {}.", filterProperties.getUrlPatterns());
        return registration;
    }
}
