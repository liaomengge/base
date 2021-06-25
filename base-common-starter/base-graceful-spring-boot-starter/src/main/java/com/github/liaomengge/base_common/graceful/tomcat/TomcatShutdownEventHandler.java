package com.github.liaomengge.base_common.graceful.tomcat;

import com.github.liaomengge.base_common.graceful.GracefulProperties;
import com.github.liaomengge.base_common.graceful.consts.GracefulConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2021/2/24.
 * 注：
 * {@link com.alibaba.cloud.nacos.registry.NacosServiceRegistry#close()}关闭时，未判断namingService是否为空，会出现NPE异常
 */
@Slf4j
public class TomcatShutdownEventHandler implements TomcatConnectorCustomizer,
        ApplicationListener<ContextClosedEvent> {
    
    private volatile Connector connector;

    @Autowired
    private GracefulProperties gracefulProperties;

    @Override
    public void customize(Connector connector) {
        this.connector = connector;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        if (Objects.nonNull(this.connector)) {
            this.connector.pause();
            log.info("paused {} to stop accepting new requests", connector);

            ThreadPoolExecutor executor = this.getThreadPoolExecutor();
            if (Objects.nonNull(executor)) {
                executor.shutdown();
                log.info("tomcat shutdown start...");
                if (this.gracefulProperties.getTimeout() > 0) {
                    try {
                        for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                            int awaitTime = Math.min(remaining, GracefulConst.CHECK_INTERVAL);
                            try {
                                if (executor.awaitTermination(awaitTime, TimeUnit.SECONDS)) {
                                    break;
                                }
                            } catch (InterruptedException e) {
                                log.warn("Interrupted while waiting for executor [tomcat] to terminate");
                                Thread.currentThread().interrupt();
                            }
                            log.info("{} thread(s) active, after {} seconds run over", executor.getActiveCount(),
                                    awaitTime);
                        }
                    } catch (Exception e) {
                        log.info("tomcat shutdown exception", e);
                    }
                }
                log.info("tomcat shutdown end...");
            }
        }
    }

    private ThreadPoolExecutor getThreadPoolExecutor() {
        Executor executor = this.connector.getProtocolHandler().getExecutor();

        if (executor instanceof ThreadPoolExecutor) {
            return (ThreadPoolExecutor) executor;
        }
        return null;
    }
}
