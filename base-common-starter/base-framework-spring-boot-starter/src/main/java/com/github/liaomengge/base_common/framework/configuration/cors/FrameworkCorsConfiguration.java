package com.github.liaomengge.base_common.framework.configuration.cors;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * Created by liaomengge on 2020/8/4.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "base.framework.cors.enabled", havingValue = "true")
public class FrameworkCorsConfiguration {

    @Bean("corsFilterRegistration")
    @ConditionalOnMissingBean(name = "corsFilterRegistration")
    public FilterRegistrationBean corsFilterRegistration(FrameworkProperties frameworkProperties) {
        FrameworkProperties.CorsProperties corsProperties = frameworkProperties.getCors();

        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //是否发送Cookie信息
        corsConfiguration.setAllowCredentials(corsProperties.isAllowCredentials());
        //放行哪些原始域
        corsConfiguration.setAllowedOrigins(corsProperties.getAllowedOrigins());
        //放行哪些原始域(请求方式)
        corsConfiguration.setAllowedMethods(corsProperties.getAllowedMethods());
        //放行哪些原始域(头部信息)
        corsConfiguration.setAllowedHeaders(corsProperties.getAllowedHeaders());
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        corsConfiguration.setExposedHeaders(corsProperties.getExposedHeaders());
        corsConfiguration.setMaxAge(corsProperties.getMaxAge());

        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration(corsProperties.getPath(), corsConfiguration);

        FilterRegistrationBean registration = new FilterRegistrationBean(new CorsFilter(configSource));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setEnabled(corsProperties.isEnabled());
        return registration;
    }

}
