package com.github.liaomengge.base_common.webflux.context;

import com.github.liaomengge.base_common.utils.threadlocal.LyThreadLocalUtil;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;

import java.util.Objects;
import java.util.Optional;

/**
 * Created by liaomengge on 2021/2/8.
 * see {@link org.springframework.web.filter.reactive.ServerWebExchangeContextFilter}
 */
public class ReactorRequestContextHolder {

    private static final ThreadLocal<ServerWebExchange> EXCHANGE_CONTEXT_THREAD_LOCAL =
            LyThreadLocalUtil.getNamedTransmittableThreadLocal("EXCHANGE_CONTEXT");

    public static void putExchange(ServerWebExchange exchange) {
        if (Objects.nonNull(exchange)) {
            EXCHANGE_CONTEXT_THREAD_LOCAL.set(exchange);
        }
    }

    public static ServerWebExchange getExchange() {
        return EXCHANGE_CONTEXT_THREAD_LOCAL.get();
    }

    public static ServerHttpRequest getRequest() {
        return Optional.ofNullable(getExchange()).map(ServerWebExchange::getRequest).orElse(null);
    }

    public static void removeExchange() {
        EXCHANGE_CONTEXT_THREAD_LOCAL.remove();
    }
}
