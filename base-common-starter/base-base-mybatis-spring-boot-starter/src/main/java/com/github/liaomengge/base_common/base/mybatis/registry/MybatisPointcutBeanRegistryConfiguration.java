package com.github.liaomengge.base_common.base.mybatis.registry;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/5/17.
 */
@Configuration
@ConditionalOnProperty(prefix = "base.mybatis", name = "aop", havingValue = "true")
public class MybatisPointcutBeanRegistryConfiguration {

    @Bean
    public static MybatisPointcutBeanDefinitionRegistry mybatisPointcutBeanDefinitionRegistry() {
        return new MybatisPointcutBeanDefinitionRegistry();
    }
}
