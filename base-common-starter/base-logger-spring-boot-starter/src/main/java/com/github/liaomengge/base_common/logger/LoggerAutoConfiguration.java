package com.github.liaomengge.base_common.logger;

import com.github.liaomengge.base_common.logger.servlet.LoggerServlet;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.logging.LoggersEndpointAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/1/21.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LoggerProperties.class)
@AutoConfigureAfter({EnableAutoConfiguration.class, LoggersEndpointAutoConfiguration.class})
public class LoggerAutoConfiguration {

    private final LoggerProperties loggerProperties;

    @RefreshScope
    @Bean
    public LoggerServlet loggerServlet() {
        return new LoggerServlet();
    }

    @Bean
    @ConditionalOnBean(LoggerServlet.class)
    public ServletRegistrationBean loggerServletRegistrationBean(LoggerServlet loggerServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(loggerServlet);
        registration.addUrlMappings(loggerProperties.getContextPath());
        return registration;
    }
}
