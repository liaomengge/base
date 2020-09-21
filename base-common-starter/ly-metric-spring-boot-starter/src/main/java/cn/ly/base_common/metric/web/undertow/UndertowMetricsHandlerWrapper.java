package cn.ly.base_common.metric.web.undertow;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.MetricsHandler;
import lombok.Getter;

/**
 * Created by liaomengge on 2020/9/16.
 */
public class UndertowMetricsHandlerWrapper implements HandlerWrapper {

    @Getter
    private MetricsHandler metricsHandler;

    @Override
    public HttpHandler wrap(HttpHandler handler) {
        metricsHandler = new MetricsHandler(handler);
        return metricsHandler;
    }
}
