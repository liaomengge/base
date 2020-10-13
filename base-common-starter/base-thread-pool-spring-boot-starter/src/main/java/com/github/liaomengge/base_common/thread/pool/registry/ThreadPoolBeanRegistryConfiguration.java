package com.github.liaomengge.base_common.thread.pool.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Configuration
public class ThreadPoolBeanRegistryConfiguration {

    @Bean
    public static ThreadPoolBeanDefinitionRegistry threadPoolBeanDefinitionRegistry() {
        return new ThreadPoolBeanDefinitionRegistry();
    }
}
