package com.github.liaomengge.base_common.feign.client;

import com.alibaba.cloud.sentinel.feign.SentinelFeign;
import com.alibaba.csp.sentinel.SphU;
import com.netflix.hystrix.HystrixCommand;
import feign.Feign;
import feign.Retryer;
import feign.hystrix.HystrixFeign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.FeignClientsConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by liaomengge on 2020/10/28.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(FeignClientsConfiguration.class)
public class FeignClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Retryer feignRetryer() {
        return Retryer.NEVER_RETRY;
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public Feign.Builder feignBuilder(Retryer retryer) {
        return Feign.builder().decode404().retryer(retryer);
    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({HystrixCommand.class, HystrixFeign.class})
    protected static class HystrixFeignConfiguration {

        @Bean
        @Scope("prototype")
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "feign.hystrix.enabled")
        public Feign.Builder feignHystrixBuilder() {
            return HystrixFeign.builder().decode404();
        }

    }

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass({SphU.class, Feign.class})
    protected static class SentinelFeignConfiguration {

        @Bean
        @Scope("prototype")
        @ConditionalOnMissingBean
        @ConditionalOnProperty(name = "feign.sentinel.enabled")
        public Feign.Builder feignSentinelBuilder() {
            return SentinelFeign.builder().decode404();
        }
    }
}
