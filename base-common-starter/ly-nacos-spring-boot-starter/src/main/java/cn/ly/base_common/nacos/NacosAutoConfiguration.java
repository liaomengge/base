package cn.ly.base_common.nacos;

import cn.ly.base_common.nacos.endpoint.NacosPullInEndpoint;
import cn.ly.base_common.nacos.endpoint.NacosPullOutEndpoint;
import cn.ly.base_common.nacos.endpoint.process.NacosEndpointBeanPostProcess;
import com.alibaba.cloud.nacos.ConditionalOnNacosDiscoveryEnabled;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.ConditionalOnDiscoveryEnabled;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2020/8/17.
 */
@Configuration
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
    public NacosPullOutEndpoint nacosPullOutEndpoint() {
        return new NacosPullOutEndpoint();
    }

    @Bean
    public NacosEndpointBeanPostProcess nacosEndpointBeanPostProcess() {
        return new NacosEndpointBeanPostProcess();
    }
}
