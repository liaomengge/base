package com.github.liaomengge.base_common.framework.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.service.base_framework.common.controller.GlobalErrorAttributes;
import com.github.liaomengge.service.base_framework.common.controller.GlobalErrorController;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Servlet.class, DispatcherServlet.class, GlobalErrorAttributes.class, GlobalErrorController.class})
@ConditionalOnWebApplication
public class FrameworkErrorConfiguration {

    private final ObjectMapper objectMapper;
    private final ServerProperties serverProperties;

    public FrameworkErrorConfiguration(ObjectProvider<ObjectMapper> objectProvider,
                                       ServerProperties serverProperties) {
        this.objectMapper = objectProvider.getIfAvailable();
        this.serverProperties = serverProperties;
    }

    @Bean("com.github.liaomengge.service.base_framework.common.controller.GlobalErrorAttributes")
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public GlobalErrorAttributes errorAttributes() {
        return new GlobalErrorAttributes();
    }

    @Bean("com.github.liaomengge.service.base_framework.common.controller.GlobalErrorController")
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public GlobalErrorController globalErrorController(ErrorAttributes errorAttributes) {
        return new GlobalErrorController(errorAttributes, serverProperties.getError(), objectMapper);
    }
}
