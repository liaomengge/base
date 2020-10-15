package com.github.liaomengge.base_common.framework;

import com.github.liaomengge.base_common.framework.configuration.FrameworkCorsConfiguration;
import com.github.liaomengge.base_common.framework.configuration.FrameworkMicrometerConfiguration;
import com.github.liaomengge.base_common.framework.configurer.FrameworkWebMvcConfigurer;
import com.github.liaomengge.base_common.framework.error.FrameworkErrorConfiguration;
import com.github.liaomengge.base_common.framework.registry.FrameworkBeanRegistryConfiguration;
import com.github.liaomengge.base_common.framework.selector.FilterConfiguration;
import com.github.liaomengge.base_common.support.spring.SpringUtils;
import com.github.liaomengge.service.base_framework.common.config.FilterConfig;
import com.github.liaomengge.service.base_framework.common.filter.aspect.ServiceApiAspect;
import com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain;
import com.github.liaomengge.service.base_framework.common.filter.chain.ServiceApiFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2018/12/20.
 */
@Configuration
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(FrameworkProperties.class)
@Import({FrameworkBeanRegistryConfiguration.class, FrameworkErrorConfiguration.class,
        FrameworkWebMvcConfigurer.class, FilterConfiguration.class,
        FrameworkCorsConfiguration.class, FrameworkMicrometerConfiguration.class})
public class FrameworkAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean("com.github.liaomengge.base_common.support.spring.SpringUtils")
    @ConditionalOnMissingBean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean("com.github.liaomengge.service.base_framework.common.config.FilterConfig")
    @ConditionalOnMissingBean
    @ConfigurationProperties("base.framework")
    public FilterConfig filterConfig() {
        return new FilterConfig();
    }

    @Bean("com.github.liaomengge.service.base_framework.common.filter.chain.FilterChain")
    @ConditionalOnMissingBean
    public FilterChain filterChain() {
        FilterChain filterChain = new FilterChain();
        Map<String, ServiceApiFilter> serviceFilterMap = applicationContext.getBeansOfType(ServiceApiFilter.class);
        Optional.ofNullable(serviceFilterMap).ifPresent(val -> filterChain.addFilter(val.values().parallelStream().collect(Collectors.toList())));
        return filterChain;
    }

    @Bean("com.github.liaomengge.service.base_framework.common.filter.aspect.ServiceApiAspect")
    @ConditionalOnMissingBean
    public ServiceApiAspect serviceApiAspect(FilterConfig filterConfig, FilterChain filterChain) {
        ServiceApiAspect serviceAspect = new ServiceApiAspect();
        serviceAspect.setFilterConfig(filterConfig);
        serviceAspect.setFilterChain(filterChain);
        return serviceAspect;
    }
}
