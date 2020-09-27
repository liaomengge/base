package cn.ly.base_common.metric.web.tomcat;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.tomcat.TomcatMetrics;
import org.apache.catalina.Manager;
import org.springframework.boot.actuate.autoconfigure.metrics.web.tomcat.TomcatMetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/9/16.
 */
@Configuration
@ConditionalOnClass({TomcatMetrics.class, Manager.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(prefix = "ly.metric.web.tomcat", name = "enabled", matchIfMissing = true)
@AutoConfigureBefore(TomcatMetricsAutoConfiguration.class)
public class TomcatMeterConfiguration {

    @Bean
    @ConditionalOnClass(MeterRegistry.class)
    @ConditionalOnMissingBean({TomcatMetrics.class, TomcatMeterBinder.class})
    public TomcatMeterBinder tomcatMeterBinder(MeterRegistry meterRegistry) {
        return new TomcatMeterBinder(meterRegistry);
    }
}
