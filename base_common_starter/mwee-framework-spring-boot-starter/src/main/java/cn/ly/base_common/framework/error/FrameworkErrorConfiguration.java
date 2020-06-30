package cn.ly.base_common.framework.error;

import cn.mwee.service.base_framework.common.controller.GlobalErrorAttributes;
import cn.mwee.service.base_framework.common.controller.GlobalErrorController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2019/11/29.
 */
@Configuration
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

    @Bean
    @ConditionalOnMissingBean(value = ErrorAttributes.class, search = SearchStrategy.CURRENT)
    public GlobalErrorAttributes errorAttributes() {
        return new GlobalErrorAttributes();
    }

    @Bean
    @ConditionalOnMissingBean(value = ErrorController.class, search = SearchStrategy.CURRENT)
    public GlobalErrorController globalErrorController(ErrorAttributes errorAttributes) {
        return new GlobalErrorController(errorAttributes, serverProperties.getError(), objectMapper);
    }
}
