package cn.ly.base_common.metric.metrics.thread.tomcat;

import cn.ly.base_common.metric.metrics.thread.AbstractPublicMetrics;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.springframework.boot.actuate.metrics.Metric;
import org.springframework.boot.context.embedded.EmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by liaomengge on 2019/5/31.
 */
public class MwTomcatPublicMetrics extends AbstractPublicMetrics {

    @Override
    public Collection<Metric<?>> metrics() {
        if (this.applicationContext instanceof EmbeddedWebApplicationContext) {
            EmbeddedServletContainer embeddedServletContainer = ((EmbeddedWebApplicationContext) applicationContext)
                    .getEmbeddedServletContainer();
            if (embeddedServletContainer instanceof TomcatEmbeddedServletContainer) {
                Connector connector =
                        ((TomcatEmbeddedServletContainer) embeddedServletContainer).getTomcat().getConnector();
                if (Objects.nonNull(connector)) {
                    ProtocolHandler handler = connector.getProtocolHandler();
                    ThreadPoolExecutor executor = (ThreadPoolExecutor) handler.getExecutor();
                    return addMetric("tomcat", executor);
                }
            }
        }
        return Collections.emptySet();
    }
}
