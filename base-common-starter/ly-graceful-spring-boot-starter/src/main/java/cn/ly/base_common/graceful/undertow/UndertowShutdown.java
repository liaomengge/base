package cn.ly.base_common.graceful.undertow;

import cn.ly.base_common.graceful.GracefulProperties;
import cn.ly.base_common.graceful.consts.GracefulConst;
import cn.ly.base_common.utils.collection.LyCollectionUtil;
import io.undertow.Undertow;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UndertowShutdown implements ApplicationListener<ContextClosedEvent> {

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
