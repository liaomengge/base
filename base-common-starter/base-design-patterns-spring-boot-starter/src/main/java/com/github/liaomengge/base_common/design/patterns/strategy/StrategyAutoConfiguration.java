package com.github.liaomengge.base_common.design.patterns.strategy;

import com.github.liaomengge.base_common.design.patterns.strategy.factory.StrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2021/6/3.
 */
@Configuration(proxyBeanMethods = false)
public class StrategyAutoConfiguration {

    @Bean
    public StrategyFactory strategyFactory() {
        return new StrategyFactory();
    }
}
