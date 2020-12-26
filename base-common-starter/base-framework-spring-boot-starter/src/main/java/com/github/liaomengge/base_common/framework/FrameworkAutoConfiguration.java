package com.github.liaomengge.base_common.framework;

import com.github.liaomengge.base_common.framework.advice.FrameworkResponseBodyAdviceConfiguration;
import com.github.liaomengge.base_common.framework.configuration.convert.FrameworkWebMvcConfigurer;
import com.github.liaomengge.base_common.framework.configuration.cors.FrameworkCorsConfiguration;
import com.github.liaomengge.base_common.framework.configuration.micrometer.FrameworkMicrometerConfiguration;
import com.github.liaomengge.base_common.framework.configuration.request.FrameworkRequestConfiguration;
import com.github.liaomengge.base_common.framework.configuration.xss.FrameworkXssConfiguration;
import com.github.liaomengge.base_common.framework.error.FrameworkErrorConfiguration;
import com.github.liaomengge.base_common.framework.registry.FrameworkBeanRegistryConfiguration;
import com.github.liaomengge.base_common.framework.selector.FilterConfiguration;
import com.github.liaomengge.base_common.support.spring.SpringUtils;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/12/20.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore(ErrorMvcAutoConfiguration.class)
@EnableConfigurationProperties(FrameworkProperties.class)
@Import({FrameworkResponseBodyAdviceConfiguration.class, FrameworkBeanRegistryConfiguration.class,
        FrameworkErrorConfiguration.class, FilterConfiguration.class, FrameworkWebMvcConfigurer.class,
        FrameworkCorsConfiguration.class, FrameworkXssConfiguration.class, FrameworkRequestConfiguration.class,
        FrameworkMicrometerConfiguration.class})
public class FrameworkAutoConfiguration {

    @Bean("com.github.liaomengge.base_common.support.spring.SpringUtils")
    @ConditionalOnMissingBean
    public SpringUtils springUtils() {
        return new SpringUtils();
    }
}
