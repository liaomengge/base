package cn.ly.base_common.metric.metrics.thread.tomcat;

import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * Created by liaomengge on 2019/5/31.
 */
@Configuration
@ConditionalOnClass({Servlet.class, Tomcat.class})
@ConditionalOnWebApplication
public class TomcatMetricsConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TomcatPublicMetrics lyTomcatPublicMetrics() {
        return new TomcatPublicMetrics();
    }
}
