package com.github.liaomengge.base_common.feign.hystrix;

import com.github.liaomengge.base_common.feign.hystrix.initializer.FeignHystrixInitializer;
import com.github.liaomengge.base_common.feign.hystrix.strategy.FeignHystrixConcurrencyStrategy;
import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.netflix.hystrix.HystrixCommand;
import feign.hystrix.HystrixFeign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignHystrixTargeter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/8.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(name = "feign.hystrix.enabled")
@ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
public class FeignHystrixAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public FeignHystrixConcurrencyStrategy feignHystrixConcurrencyStrategy() {
        return new FeignHystrixConcurrencyStrategy();
    }

    @Bean
    @ConditionalOnMissingBean(FeignHystrixInitializer.class)
    public FeignHystrixInitializer feignHystrixInitializer(FeignClientManager feignClientManager) {
        return new FeignHystrixInitializer(feignClientManager);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnClass(name = "feign.hystrix.HystrixFeign")
    public FeignHystrixTargeter targeter() {
        return new FeignHystrixTargeter();
    }
}
