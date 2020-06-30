package cn.ly.base_common.framework;

import cn.ly.base_common.framework.configurer.FrameworkWebMvcConfigurer;
import cn.ly.base_common.framework.error.FrameworkErrorConfiguration;
import cn.ly.base_common.framework.registry.FrameworkBeanRegistryConfiguration;
import cn.ly.base_common.framework.selector.FilterConfiguration;
import cn.mwee.base_common.support.spring.SpringUtils;
import cn.mwee.service.base_framework.common.config.FilterConfig;
import cn.mwee.service.base_framework.common.filter.aspect.ServiceAspect;
import cn.mwee.service.base_framework.common.filter.chain.FilterChain;
import cn.mwee.service.base_framework.common.filter.chain.ServiceFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration;
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
        FrameworkWebMvcConfigurer.class, FilterConfiguration.class})
public class FrameworkAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean("cn.mwee.base_common.support.spring.SpringUtils")
    @ConditionalOnMissingBean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }

    @Bean("cn.mwee.service.base_framework.common.config.FilterConfig")
    @ConditionalOnMissingBean
    @ConfigurationProperties("mwee.framework")
    public FilterConfig filterConfig() {
        return new FilterConfig();
    }

    @Bean("cn.mwee.service.base_framework.common.filter.chain.FilterChain")
    @ConditionalOnMissingBean
    public FilterChain filterChain() {
        FilterChain filterChain = new FilterChain();
        Map<String, ServiceFilter> serviceFilterMap = applicationContext.getBeansOfType(ServiceFilter.class);
        Optional.ofNullable(serviceFilterMap).ifPresent(val -> filterChain.addFilter(val.values().parallelStream().collect(Collectors.toList())));
        return filterChain;
    }

    @Bean("cn.mwee.service.base_framework.common.filter.aspect.ServiceAspect")
    @ConditionalOnMissingBean
    public ServiceAspect serviceAspect(FilterConfig filterConfig, FilterChain filterChain) {
        ServiceAspect serviceAspect = new ServiceAspect();
        serviceAspect.setFilterConfig(filterConfig);
        serviceAspect.setFilterChain(filterChain);
        return serviceAspect;
    }
}
