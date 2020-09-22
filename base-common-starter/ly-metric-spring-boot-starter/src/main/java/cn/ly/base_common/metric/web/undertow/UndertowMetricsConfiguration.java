package cn.ly.base_common.metric.web.undertow;

import io.micrometer.core.instrument.MeterRegistry;
import io.undertow.Undertow;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/16.
 */
@Configuration
@ConditionalOnClass(Undertow.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureBefore(ServletWebServerFactoryAutoConfiguration.class)
@ConditionalOnProperty(prefix = "ly.metric.web.undertow", name = "enabled", matchIfMissing = true)
public class UndertowMetricsConfiguration {

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnMissingBean
    public UndertowMetricsBinder undertowMetricsBinder(MeterRegistry meterRegistry) {
        return new UndertowMetricsBinder(meterRegistry);
    }

    @Bean
    public WebServerFactoryCustomizer<UndertowServletWebServerFactory> undertowMetricsWebServerFactoryCustomizer(UndertowMetricsBinder undertowMetricsBinder) {
        return factory -> factory.addDeploymentInfoCustomizers(customizers -> customizers.setMetricsCollector(undertowMetricsBinder));
    }
}
