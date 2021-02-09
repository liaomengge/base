package com.github.liaomengge.base_common.webflux.filter;

import com.github.liaomengge.base_common.webflux.context.ReactorRequestContextHolder;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Created by liaomengge on 2021/2/8.
 */
public class ReactorServerWebExchangeContextFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ReactorRequestContextHolder.putExchange(exchange);
        return chain.filter(exchange).doFinally(signalType -> ReactorRequestContextHolder.removeExchange());
    }

    @Override
    public int getOrder() {
        return -1000;
    }
}
