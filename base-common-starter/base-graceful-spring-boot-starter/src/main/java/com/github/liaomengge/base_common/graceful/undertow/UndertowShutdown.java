package com.github.liaomengge.base_common.graceful.undertow;

import com.github.liaomengge.base_common.graceful.GracefulProperties;
import com.github.liaomengge.base_common.graceful.consts.GracefulConst;
import com.github.liaomengge.base_common.utils.collection.LyCollectionUtil;
import com.github.liaomengge.base_common.utils.log4j2.LyLogger;
import io.undertow.Undertow;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * Created by liaomengge on 2020/7/4.
 */
public class UndertowShutdown implements ApplicationListener<ContextClosedEvent> {

    private static final Logger log = LyLogger.getInstance(UndertowShutdown.class);

    @Autowired
    private GracefulProperties gracefulProperties;

    @Autowired
    private GracefulShutdownWrapper gracefulShutdownWrapper;

    @Autowired
    private ServletWebServerApplicationContext context;

    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        gracefulShutdownWrapper.getGracefulShutdownHandler().shutdown();
        try {
            UndertowServletWebServer webServer = (UndertowServletWebServer) context.getWebServer();
            Field field = webServer.getClass().getDeclaredField("undertow");
            field.setAccessible(true);
            Undertow undertow = (Undertow) field.get(webServer);
            Optional.ofNullable(undertow).map(Undertow::getListenerInfo).map(LyCollectionUtil::getFirst)
                    .map(Undertow.ListenerInfo::getConnectorStatistics)
                    .ifPresent(val -> {
                        for (int remaining = this.gracefulProperties.getTimeout(); remaining > 0; remaining -= GracefulConst.CHECK_INTERVAL) {
                            if (val.getActiveConnections() <= 0) {
                                continue;
                            }
                            log.info("{} thread(s) active, {} seconds remaining", val.getActiveConnections(),
                                    remaining);
                        }
                    });
        } catch (Exception e) {
            log.error("close undertow web server fail", e);
        }
    }
}
