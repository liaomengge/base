package cn.mwee.base_common.quartz.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/1/29.
 */
@Configuration
public class QuartzBeanRegistryConfiguration {

    @Bean
    public static QuartzBeanDefinitionRegistry quartzBeanDefinitionRegistry() {
        return new QuartzBeanDefinitionRegistry();
    }
}
