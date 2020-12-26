package com.github.liaomengge.base_common.thread.pool;

import com.ctrip.framework.apollo.spring.config.PropertySourcesConstants;
import com.github.liaomengge.base_common.thread.pool.listener.ThreadPoolApolloListener;
import com.github.liaomengge.base_common.thread.pool.registry.ThreadPoolBeanRegistryConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ThreadPoolGroupProperties.class)
@Import(ThreadPoolBeanRegistryConfiguration.class)
public class ThreadPoolGroupAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(PropertySourcesConstants.APOLLO_BOOTSTRAP_ENABLED)
    public ThreadPoolApolloListener threadPoolApolloListener() {
        return new ThreadPoolApolloListener();
    }
}
