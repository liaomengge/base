package com.github.liaomengge.base_common.graceful.undertow;

import com.github.liaomengge.base_common.graceful.GracefulProperties;
import com.github.liaomengge.base_common.graceful.consts.GracefulConst;
import com.github.liaomengge.base_common.graceful.undertow.handler.GracefulShutdownWrapper;
import com.github.liaomengge.base_common.utils.collection.LyCollectionUtil;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created by liaomengge on 2020/7/4.
 * 注：
 * {@link com.alibaba.cloud.nacos.discovery.NacosWatch#stop()在undertow关闭后，在获取上下文环境env的propertyName
 * ，拿不到当前的DeploymentInfo信息，故会throw异常}
 */
@Slf4j
public class UndertowShutdownEventHandler implements ApplicationListener<ContextClosedEvent> {
    
    @Autowired
    private GracefulProperties gracefulProperties;

    @Autowired
    private GracefulShutdownWrapper gracefulShutdownWrapper;

    @Autowired
    private ServletWebServerApplicationContext context;

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        gracefulShutdownWrapper.getGracefulShutdownHandler().shutdown();
        log.info("undertow shutdown start...");
        try {
            UndertowServletWebServer webServer = (UndertowServletWebServer) context.getWebServer();
            Field field = ReflectionUtils.findField(webServer.getClass(), "undertow");
            ReflectionUtils.makeAccessible(field);
            Undertow undertow = (Undertow) ReflectionUtils.getField(field, webServer);
            Optional.ofNullable(undertow).map(Undertow::getListenerInfo).map(LyCollectionUtil::getFirst)
                    .map(Undertow.ListenerInfo::getConnectorStatistics)
                    .ifPresent(connectorStatistics -> {
                        for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                            if (connectorStatistics.getActiveConnections() <= 0) {
                                break;
                            }
                            int awaitTime = Math.min(remaining, GracefulConst.CHECK_INTERVAL);
                            try {
                                TimeUnit.SECONDS.sleep(awaitTime);
                            } catch (InterruptedException e) {
                                log.warn("Interrupted while waiting for executor [undertow] to terminate");
                                Thread.currentThread().interrupt();
                            }
                            log.info("{} thread(s) active, after {} seconds run over",
                                    connectorStatistics.getActiveConnections(), awaitTime);
                        }
                    });
        } catch (Exception e) {
            log.error("close undertow web server fail", e);
        }
        log.info("undertow shutdown end...");
    }
}
