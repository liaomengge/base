package cn.ly.base_common.framework.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/3/29.
 */
@Configuration
public class FrameworkBeanRegistryConfiguration {

    @Bean
    public static FrameworkBeanDefinitionRegistry frameworkBeanDefinitionRegistry() {
        return new FrameworkBeanDefinitionRegistry();
    }
}
