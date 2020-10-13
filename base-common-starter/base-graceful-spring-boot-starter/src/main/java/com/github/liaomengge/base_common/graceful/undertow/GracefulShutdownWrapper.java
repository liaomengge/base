package com.github.liaomengge.base_common.graceful.undertow;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.GracefulShutdownHandler;
import lombok.Getter;

/**
 * Created by liaomengge on 2020/7/4.
 */
public class GracefulShutdownWrapper implements HandlerWrapper {

    @Getter
    private GracefulShutdownHandler gracefulShutdownHandler;

    @Override
    public HttpHandler wrap(HttpHandler httpHandler) {
        if (gracefulShutdownHandler == null) {
            this.gracefulShutdownHandler = new GracefulShutdownHandler(httpHandler);
        }
        return gracefulShutdownHandler;
    }
}
