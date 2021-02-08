package com.github.liaomengge.base_common.webflux.webclient;

import com.github.liaomengge.base_common.utils.number.LyNumberUtil;
import com.github.liaomengge.base_common.webflux.WebFluxProperties;
import com.github.liaomengge.base_common.webflux.webclient.header.HeaderExchangeFilterFunction;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.reactive.OnNoRibbonDefaultCondition;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.Objects;

/**
 * Created by liaomengge on 2021/2/8.
 */
@Configuration(proxyBeanMethods = false)
public class WebClientConfiguration {

    private final WebFluxProperties webFluxProperties;
    private final ReactorLoadBalancerExchangeFilterFunction exchangeFilterFunction;

    public WebClientConfiguration(WebFluxProperties webFluxProperties,
                                  ObjectProvider<ReactorLoadBalancerExchangeFilterFunction> objectProvider) {
        this.webFluxProperties = webFluxProperties;
        this.exchangeFilterFunction = objectProvider.getIfAvailable();
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderExchangeFilterFunction headerExchangeFilterFunction() {
        return new HeaderExchangeFilterFunction();
    }

    @Bean("lbWebClient")
    @ConditionalOnMissingBean(name = "lbWebClient")
    @Conditional(OnNoRibbonDefaultCondition.class)
    public WebClient lbWebClient(HeaderExchangeFilterFunction headerExchangeFilterFunction) {
        WebFluxProperties.WebClientProperties webClientProperties = this.webFluxProperties.getWebClient();
        return WebClient.builder()
                .baseUrl(webClientProperties.getBaseUrl())
                .filters(exchangeFilterFunctions -> {
                    exchangeFilterFunctions.add(headerExchangeFilterFunction);
                    if (Objects.nonNull(this.exchangeFilterFunction)) {
                        exchangeFilterFunctions.add(this.exchangeFilterFunction);
                    }
                })
                .clientConnector(new ReactorClientHttpConnector(getHttpClient(webClientProperties)))
                .build();
    }

    @Bean("webClient")
    @ConditionalOnMissingBean(name = "webClient")
    public WebClient webClient(HeaderExchangeFilterFunction headerExchangeFilterFunction) {
        WebFluxProperties.WebClientProperties webClientProperties = this.webFluxProperties.getWebClient();
        return WebClient.builder()
                .baseUrl(webClientProperties.getBaseUrl())
                .filter(headerExchangeFilterFunction)
                .clientConnector(new ReactorClientHttpConnector(getHttpClient(webClientProperties)))
                .build();
    }

    private HttpClient getHttpClient(WebFluxProperties.WebClientProperties webClientProperties) {
        return HttpClient.create()
                .tcpConfiguration(tcpClient -> tcpClient.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        LyNumberUtil.getIntValue(webClientProperties.getConnectTimeout().toMillis()))
                        .doOnConnected(connection -> connection.addHandlerLast(new ReadTimeoutHandler(LyNumberUtil.getIntValue(webClientProperties.getReadTimeout().getSeconds())))
                                .addHandlerLast(new WriteTimeoutHandler(LyNumberUtil.getIntValue(webClientProperties.getWriteTimeout().getSeconds())))));
    }
}
