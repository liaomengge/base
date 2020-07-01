package cn.ly.base_common.graceful.tomcat;

import cn.ly.base_common.graceful.GracefulProperties;
import cn.ly.base_common.graceful.consts.GracefulConst;
import cn.ly.base_common.utils.log4j2.LyLogger;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2018/12/18.
 */
public class TomcatShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {

    private static final Logger logger = LyLogger.getInstance(TomcatShutdown.class);

    private volatile Connector connector;

    private GracefulProperties gracefulProperties;

    public TomcatShutdown(GracefulProperties gracefulProperties) {
        this.gracefulProperties = gracefulProperties;
    }

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        Optional.ofNullable(this.connector).ifPresent(val -> {
            val.pause();
            ThreadPoolExecutor executor = this.getThreadPoolExecutor();
            if (Objects.nonNull(executor)) {
                executor.shutdown();
                logger.info("tomcat shutdown start...");
                try {
                    for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                        try {
                            if (executor.awaitTermination(Math.min(remaining, GracefulConst.CHECK_INTERVAL), TimeUnit.SECONDS)) {
                                continue;
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        logger.info("{} thread(s) active, {} seconds remaining", executor.getActiveCount(), remaining);
                    }
                } catch (Exception e) {
                    logger.info("tomcat shutdown exception", e);
                }
                logger.info("tomcat shutdown end...");
            }
        });
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        Executor executor = this.connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {
            return (ThreadPoolExecutor) executor;
        }
        return null;
    }
}
