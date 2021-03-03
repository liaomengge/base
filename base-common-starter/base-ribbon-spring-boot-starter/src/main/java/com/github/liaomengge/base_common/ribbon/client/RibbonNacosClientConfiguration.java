package com.github.liaomengge.base_common.ribbon.client;

import com.github.liaomengge.base_common.ribbon.ribbon.filter.ServerFilter;
import com.github.liaomengge.base_common.ribbon.ribbon.predicate.NacosDiscoveryPredicate;
import com.github.liaomengge.base_common.ribbon.ribbon.rule.NacosDiscoveryRule;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.cloud.netflix.ribbon.RibbonClientName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Created by liaomengge on 2020/12/29.
 */
@Configuration(proxyBeanMethods = false)
public class RibbonNacosClientConfiguration {

    @RibbonClientName
    private String name = "client";

    @Autowired
    private PropertiesFactory propertiesFactory;

    @Autowired(required = false)
    private List<ServerFilter> serverFilters;

    @Bean
    @ConditionalOnMissingBean
    public NacosDiscoveryPredicate nacosDiscoveryPredicate() {
        return new NacosDiscoveryPredicate();
    }

    @Bean
    @ConditionalOnMissingBean
    public IRule ribbonRule(IClientConfig config, NacosDiscoveryPredicate discoveryPredicate) {
        if (this.propertiesFactory.isSet(IRule.class, name)) {
            return this.propertiesFactory.get(IRule.class, config, name);
        }
        NacosDiscoveryRule rule = new NacosDiscoveryRule();
        rule.initWithNiwsConfig(config);
        rule.setServerFilters(serverFilters);
        return rule;
    }
}
