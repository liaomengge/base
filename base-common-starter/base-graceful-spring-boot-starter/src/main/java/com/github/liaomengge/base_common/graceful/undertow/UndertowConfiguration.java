package com.github.liaomengge.base_common.graceful.undertow;

import com.github.liaomengge.base_common.graceful.undertow.handler.GracefulShutdownWrapper;
import io.undertow.Undertow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xnio.SslClientAuthMode;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.graceful.undertow", name = "enabled")
@ConditionalOnClass({Servlet.class, Undertow.class, SslClientAuthMode.class})
public class UndertowConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public UndertowShutdownEventHandler undertowShutdownEventHandler() {
        return new UndertowShutdownEventHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public GracefulShutdownWrapper gracefulShutdownWrapper() {
        return new GracefulShutdownWrapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public UndertowShutdownWebServerFactoryCustomizer undertowShutdownWebServerFactoryCustomizer(GracefulShutdownWrapper gracefulShutdownWrapper) {
        return new UndertowShutdownWebServerFactoryCustomizer(gracefulShutdownWrapper);
    }
}
