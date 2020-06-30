package cn.ly.base_common.mq;

import cn.ly.base_common.mq.MQLockProperties.ZkProperties;
import cn.ly.base_common.mq.initializer.MQLockInitializer;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Configuration
@EnableConfigurationProperties(MQLockProperties.class)
@AutoConfigureAfter(MQAutoConfiguration.class)
public class MQLockAutoConfiguration {

    @Autowired
    private MQLockProperties mqLockProperties;

    @Bean
    @ConditionalOnMissingBean
    public ZkClient zkClient() {
        ZkProperties zkProperties = this.mqLockProperties.getZk();
        ZkClient zkClient = new ZkClient(zkProperties.getZkServers(), zkProperties.getSessionTimeout(),
                zkProperties.getConnectionTimeout());
        return zkClient;
    }

    @Bean
    @ConditionalOnBean(ZkClient.class)
    public MQLockInitializer quartzLockInitializer() {
        return new MQLockInitializer();
    }
}
