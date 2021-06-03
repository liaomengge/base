package com.github.liaomengge.base_common.feign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.liaomengge.base_common.feign.endpoint.FeignClientManagerEndpoint;
import com.github.liaomengge.base_common.feign.helper.FeignHelper;
import com.github.liaomengge.base_common.feign.interceptor.GetPojoRequestInterceptor;
import com.github.liaomengge.base_common.feign.interceptor.HeaderRequestInterceptor;
import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import feign.Feign;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import lombok.AllArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/8/25.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Feign.class, Encoder.class, RequestInterceptor.class})
@EnableConfigurationProperties(FeignProperties.class)
public class FeignAutoConfiguration {

    private final ObjectMapper objectMapper;

    @Bean
    @ConditionalOnMissingBean
    public GetPojoRequestInterceptor getPojoRequestInterceptor() {
        return new GetPojoRequestInterceptor(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    public HeaderRequestInterceptor headerRequestInterceptor() {
        return new HeaderRequestInterceptor();
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignHelper feignHelper() {
        return new FeignHelper();
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignClientManager feignClientManager(FeignHelper feignHelper, FeignProperties feignProperties) {
        return new FeignClientManager(feignHelper, feignProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public FeignClientManagerEndpoint feignClientManagerEndpoint(FeignClientManager feignClientManager) {
        return new FeignClientManagerEndpoint(feignClientManager);
    }
}
