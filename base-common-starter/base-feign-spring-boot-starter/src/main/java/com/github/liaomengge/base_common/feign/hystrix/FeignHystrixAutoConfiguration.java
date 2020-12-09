package com.github.liaomengge.base_common.feign.hystrix;

import com.netflix.hystrix.HystrixCommand;
import feign.hystrix.HystrixFeign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/8.
 */
@ConditionalOnProperty(name = "feign.hystrix.enabled")
@ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
@Configuration(proxyBeanMethods = false)
public class FeignHystrixAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "feignHystrixConcurrencyStrategy")
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }
}
