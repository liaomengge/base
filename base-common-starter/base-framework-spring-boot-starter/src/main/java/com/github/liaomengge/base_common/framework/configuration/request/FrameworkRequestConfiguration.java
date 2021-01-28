package com.github.liaomengge.base_common.framework.configuration.request;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.framework.configuration.request.filter.MutableFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/8.
 */
@Configuration(proxyBeanMethods = false)
public class FrameworkRequestConfiguration {

    @Bean("requestFilterRegistrationBean")
    @ConditionalOnMissingBean(name = "requestFilterRegistrationBean")
    public FilterRegistrationBean requestFilterRegistrationBean(FrameworkProperties frameworkProperties) {
        FrameworkProperties.RequestProperties requestProperties = frameworkProperties.getRequest();

        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(new MutableFilter());
        registration.setName("requestFilterRegistrationBean");
        registration.setOrder(requestProperties.getOrder());
        registration.addUrlPatterns(requestProperties.getUrlPatterns());
        registration.setEnabled(requestProperties.isEnabled());
        return registration;
    }
}
