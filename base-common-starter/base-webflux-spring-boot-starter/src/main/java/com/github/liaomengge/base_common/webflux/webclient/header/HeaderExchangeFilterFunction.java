package com.github.liaomengge.base_common.webflux.webclient.header;

import com.github.liaomengge.base_common.webflux.context.ReactorRequestContextHolder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

/**
 * Created by liaomengge on 2021/2/8.
 */
public class HeaderExchangeFilterFunction implements ExchangeFilterFunction {

    @Override
    public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
        ClientRequest newRequest = ClientRequest.from(clientRequest)
                .headers(httpHeaders -> httpHeaders.addAll(ReactorRequestContextHolder.getRequest().getHeaders()))
                .build();
        return exchangeFunction.exchange(newRequest);
    }
}
