package com.github.liaomengge.base_common.feign.sentinel;

import com.alibaba.csp.sentinel.SphU;
import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.github.liaomengge.base_common.feign.sentinel.initializer.FeignSentinelInitializer;
import feign.Feign;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/11.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SphU.class, Feign.class})
public class FeignSentinelAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "feign.sentinel.enabled")
    @ConditionalOnMissingBean(FeignSentinelInitializer.class)
    public FeignSentinelInitializer feignSentinelInitializer(FeignClientManager feignClientManager) {
        return new FeignSentinelInitializer(feignClientManager);
    }
}