package cn.ly.base_common.quartz.lock;

import cn.ly.base_common.quartz.lock.initializer.QuartzLockInitializer;
import lombok.AllArgsConstructor;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

/**
 * Created by liaomengge on 2019/5/21.
 */
@AllArgsConstructor
@Configuration
@EnableConfigurationProperties(QuartzLockProperties.class)
@ConditionalOnSingleCandidate(SchedulerFactoryBean.class)
public class QuartzLockAutoConfiguration {

    private final QuartzLockProperties quartzLockProperties;

    @Bean
    @ConditionalOnMissingBean
    public ZkClient zkClient() {
        QuartzLockProperties.ZkProperties zkProperties = this.quartzLockProperties.getZk();
        ZkClient zkClient = new ZkClient(zkProperties.getZkServers(), zkProperties.getSessionTimeout(),
                zkProperties.getConnectionTimeout());
        return zkClient;
    }

    @Bean
    @ConditionalOnBean(ZkClient.class)
    public QuartzLockInitializer quartzLockInitializer() {
        return new QuartzLockInitializer();
    }
}
