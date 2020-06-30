package cn.ly.base_common.logger;

import cn.ly.base_common.logger.servlet.LoggerServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.endpoint.LoggersEndpoint;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/1/21.
 */
@Configuration
@EnableConfigurationProperties(LoggerProperties.class)
@AutoConfigureAfter(EndpointAutoConfiguration.class)
@ConditionalOnBean(LoggersEndpoint.class)
public class LoggerAutoConfiguration {

    @Autowired
    private LoggerProperties loggerProperties;

    @RefreshScope
    @Bean
    public LoggerServlet loggerServlet() {
        return new LoggerServlet();
    }

    @Bean
    public ServletRegistrationBean loggerServletRegistrationBean(LoggerServlet loggerServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(loggerServlet);
        registration.addUrlMappings(loggerProperties.getContextPath());
        return registration;
    }
}
