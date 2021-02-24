package com.github.liaomengge.base_common.graceful.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.UpgradeProtocol;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "base.graceful.tomcat", name = "enabled")
@ConditionalOnClass({Servlet.class, Tomcat.class, UpgradeProtocol.class})
public class TomcatConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TomcatShutdownEventHandler tomcatShutdownEventHandler() {
        return new TomcatShutdownEventHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public TomcatShutdownWebServerFactoryCustomizer tomcatShutdownWebServerFactoryCustomizer(TomcatShutdownEventHandler tomcatShutdownEventHandler) {
        return new TomcatShutdownWebServerFactoryCustomizer(tomcatShutdownEventHandler);
    }
}
