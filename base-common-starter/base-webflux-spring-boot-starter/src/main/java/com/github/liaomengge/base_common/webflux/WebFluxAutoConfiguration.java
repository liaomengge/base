package com.github.liaomengge.base_common.webflux;

import com.github.liaomengge.base_common.webflux.filter.ReactorServerWebExchangeContextFilter;
import com.github.liaomengge.base_common.webflux.webclient.WebClientConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2021/2/8.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(WebFluxProperties.class)
@Import(WebClientConfiguration.class)
public class WebFluxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ReactorServerWebExchangeContextFilter reactorServerWebExchangeContextFilter() {
        return new ReactorServerWebExchangeContextFilter();
    }
}
