package cn.ly.base_common.dayu.sentinel.filter;

import cn.ly.base_common.dayu.sentinel.SentinelProperties;
import cn.ly.base_common.utils.log4j2.LyLogger;

import com.google.common.collect.ImmutableMap;
import com.timgroup.statsd.StatsDClient;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/11/7.
 */
@Configuration
@ConditionalOnWebApplication
public class SentinelWebConfiguration {

    private static final Logger log = LyLogger.getInstance(SentinelWebConfiguration.class);

    @Autowired
    private SentinelProperties sentinelProperties;

    @Bean("sentinelFilter")
    public SentinelFilter sentinelFilter(StatsDClient statsDClient) {
        return new SentinelFilter(statsDClient);
    }

    @Bean("sentinelFilterRegistrationBean")
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
        registration.setOrder(filterProperties.getOrder());
        log.info("[Sentinel Starter] register Sentinel with urlPatterns: {}.", filterProperties.getUrlPatterns());
        return registration;
    }
}
