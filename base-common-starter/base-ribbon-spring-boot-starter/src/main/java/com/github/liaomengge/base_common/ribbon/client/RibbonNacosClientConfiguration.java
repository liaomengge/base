package com.github.liaomengge.base_common.ribbon.client;

import com.github.liaomengge.base_common.ribbon.loadbalance.RibbonNacosRandomWeightRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/12/29.
 */
@Configuration(proxyBeanMethods = false)
public class RibbonNacosClientConfiguration {

    @RibbonClientName
    private String name = "client";

    private final PropertiesFactory propertiesFactory;

    public RibbonNacosClientConfiguration(PropertiesFactory propertiesFactory) {
        this.propertiesFactory = propertiesFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public IRule ribbonRule(IClientConfig config) {
        if (this.propertiesFactory.isSet(IRule.class, name)) {
            return this.propertiesFactory.get(IRule.class, config, name);
        }
        return new RibbonNacosRandomWeightRule();
    }
}
