package com.github.liaomengge.base_common.graceful.undertow;

import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.graceful.undertow", name = "enabled")
@ConditionalOnClass({HandlerWrapper.class, HttpHandler.class, UndertowServletWebServer.class, Undertow.class,
        ApplicationListener.class})
public class UndertowAutoConfiguration {

    private final GracefulShutdownWrapper gracefulShutdownWrapper;

    public UndertowAutoConfiguration(GracefulShutdownWrapper gracefulShutdownWrapper) {
        this.gracefulShutdownWrapper = gracefulShutdownWrapper;
    }

    @Bean
    public UndertowServletWebServerFactory servletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addOuterHandlerChainWrapper(gracefulShutdownWrapper));
        factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true));
        return factory;
    }
}
