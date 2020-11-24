package com.github.liaomengge.base_common.framework.configuration.xss;

import com.github.liaomengge.base_common.framework.FrameworkProperties;
import com.github.liaomengge.base_common.framework.configuration.xss.filter.XssFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.DispatcherType;

/**
 * Created by liaomengge on 2020/10/17.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "base.framework.xss.enabled", havingValue = "true")
public class FrameworkXssConfiguration {

    @Bean("xssFilterRegistration")
    @ConditionalOnMissingBean(name = "xssFilterRegistration")
    public FilterRegistrationBean<XssFilter> xssFilterRegistration(FrameworkProperties frameworkProperties) {
        FrameworkProperties.XssProperties xssProperties = frameworkProperties.getXss();

        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setDispatcherTypes(DispatcherType.REQUEST);
        registration.setFilter(new XssFilter());
        registration.setOrder(xssProperties.getOrder());
        registration.addUrlPatterns(xssProperties.getUrlPatterns());
        registration.setEnabled(xssProperties.isEnabled());
        return registration;
    }
}
