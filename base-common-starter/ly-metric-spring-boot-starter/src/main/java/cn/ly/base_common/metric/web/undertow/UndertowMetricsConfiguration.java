package cn.ly.base_common.metric.web.undertow;

import io.micrometer.core.instrument.MeterRegistry;
import io.undertow.Undertow;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.embedded.undertow.UndertowDeploymentInfoCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2020/9/16.
 */
@Configuration
@ConditionalOnClass({Servlet.class, Undertow.class})
@ConditionalOnProperty(prefix = "ly.metric.web.undertow", name = "enabled", matchIfMissing = true)
public class UndertowMetricsConfiguration {

    @Bean
    public UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper() {
        return new UndertowMetricsHandlerWrapper();
    }

    @Bean
    public UndertowDeploymentInfoCustomizer undertowDeploymentInfoCustomizer(UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return deploymentInfo -> deploymentInfo.addOuterHandlerChainWrapper(undertowMetricsHandlerWrapper);
    }

    @Bean
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public UndertowMeterBinder undertowMeterBinder(MeterRegistry meterRegistry,
                                                   UndertowMetricsHandlerWrapper undertowMetricsHandlerWrapper) {
        return new UndertowMeterBinder(meterRegistry, undertowMetricsHandlerWrapper);
    }
}