package com.github.liaomengge.base_common.feign.sentinel;

import com.alibaba.cloud.sentinel.feign.SentinelFeignAutoConfiguration;
import com.alibaba.csp.sentinel.SphU;
import com.github.liaomengge.base_common.feign.manager.FeignClientManager;
import com.github.liaomengge.base_common.feign.sentinel.feign.SentinelFeign;
import com.github.liaomengge.base_common.feign.sentinel.initializer.FeignSentinelInitializer;
import feign.Feign;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * Created by liaomengge on 2020/12/11.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({SphU.class, Feign.class})
@ConditionalOnProperty(name = "feign.sentinel.enabled")
@AutoConfigureBefore(SentinelFeignAutoConfiguration.class)
public class FeignSentinelAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(FeignSentinelInitializer.class)
    public FeignSentinelInitializer feignSentinelInitializer(FeignClientManager feignClientManager) {
        return new FeignSentinelInitializer(feignClientManager);
    }

    @Bean
    @Scope("prototype")
    @ConditionalOnMissingBean
    public Feign.Builder feignSentinelBuilder() {
        return SentinelFeign.builder().decode404();
    }
}
