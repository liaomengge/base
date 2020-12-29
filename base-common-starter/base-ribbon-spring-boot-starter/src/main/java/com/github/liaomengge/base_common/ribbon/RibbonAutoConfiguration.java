package com.github.liaomengge.base_common.ribbon;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.ribbon.ConditionalOnRibbonNacos;
import com.github.liaomengge.base_common.ribbon.client.RibbonNacosClientConfiguration;
import com.netflix.client.IClient;
import com.netflix.ribbon.Ribbon;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;

/**
 * Created by liaomengge on 2020/12/22.
 */
@Configuration(proxyBeanMethods = false)
@Conditional(RibbonAutoConfiguration.RibbonClassesConditions.class)
@EnableConfigurationProperties(RibbonProperties.class)
public class RibbonAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @EnableConfigurationProperties
    @ConditionalOnRibbonNacos
    @ConditionalOnNacosDiscoveryEnabled
    @RibbonClients(defaultConfiguration = RibbonNacosClientConfiguration.class)
    protected static class NacosRibbonConfiguration {
    }

    static class RibbonClassesConditions extends AllNestedConditions {

        RibbonClassesConditions() {
            super(ConfigurationPhase.PARSE_CONFIGURATION);
        }

        @ConditionalOnClass(IClient.class)
        static class IClientPresent {

        }

        @ConditionalOnClass(RestTemplate.class)
        static class RestTemplatePresent {

        }

        @ConditionalOnClass(AsyncRestTemplate.class)
        static class AsyncRestTemplatePresent {

        }

        @ConditionalOnClass(Ribbon.class)
        static class RibbonPresent {

        }

    }
}
