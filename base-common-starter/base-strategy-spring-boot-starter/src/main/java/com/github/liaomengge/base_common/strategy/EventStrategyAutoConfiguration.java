package com.github.liaomengge.base_common.strategy;

import com.github.liaomengge.base_common.strategy.factory.EventStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2021/6/3.
 */
@Configuration(proxyBeanMethods = false)
public class EventStrategyAutoConfiguration {

    @Bean
    public EventStrategyFactory eventStrategyFactory() {
        return new EventStrategyFactory();
    }
}
