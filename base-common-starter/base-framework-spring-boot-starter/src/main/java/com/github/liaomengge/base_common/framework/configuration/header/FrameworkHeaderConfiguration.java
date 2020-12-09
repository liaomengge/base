package com.github.liaomengge.base_common.framework.configuration.header;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.framework.configuration.header.filter.MutableFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/8.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "base.framework.header.enabled", havingValue = "true")
public class FrameworkHeaderConfiguration {

    @Bean("headerFilterRegistrationBean")
    @ConditionalOnMissingBean(name = "headerFilterRegistrationBean")
    public FilterRegistrationBean headerFilterRegistrationBean(FrameworkProperties frameworkProperties) {
        FrameworkProperties.HeaderProperties headerProperties = frameworkProperties.getHeader();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MutableFilter());
        registration.setName("headerFilterRegistrationBean");
        registration.setOrder(headerProperties.getOrder());
        registration.addUrlPatterns(headerProperties.getUrlPatterns());
        registration.setEnabled(headerProperties.isEnabled());
        return registration;
    }
}
