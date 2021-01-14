package com.github.liaomengge.base_common.nacos;

import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import com.alibaba.cloud.nacos.registry.NacosRegistrationCustomizer;
import com.github.liaomengge.base_common.nacos.consts.NacosConst;
import com.github.liaomengge.base_common.nacos.endpoint.NacosPullInEndpoint;
import com.github.liaomengge.base_common.nacos.endpoint.NacosPullOutEndpoint;
import com.github.liaomengge.base_common.nacos.endpoint.process.NacosEndpointBeanPostProcess;
import com.github.liaomengge.base_common.utils.date.LyJdk8DateUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/8/17.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnDiscoveryEnabled
@ConditionalOnNacosDiscoveryEnabled
@EnableConfigurationProperties(NacosProperties.class)
public class NacosAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public NacosPullInEndpoint nacosPullInEndpoint() {
        return new NacosPullInEndpoint();
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosPullOutEndpoint nacosPullOutEndpoint() {
        return new NacosPullOutEndpoint();
    }

    @Bean
    public NacosRegistrationCustomizer nacosRegistrationCustomizer() {
        return registration ->
                registration.getMetadata().put(NacosConst.MetadataConst.PRESERVED_REGISTER_TIME,
                        LyJdk8DateUtil.getNowDate2String());
    }

    @Bean
    public static NacosEndpointBeanPostProcess nacosEndpointBeanPostProcess() {
        return new NacosEndpointBeanPostProcess();
    }
}
