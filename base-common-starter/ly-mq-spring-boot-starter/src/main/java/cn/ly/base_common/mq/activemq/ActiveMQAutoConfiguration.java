package cn.ly.base_common.mq.activemq;

import cn.ly.base_common.helper.metric.activemq.ActiveMQMonitor;
import cn.ly.base_common.mq.activemq.ActiveMQProperties.Pool;
import cn.ly.base_common.mq.activemq.pool.MonitorPooledConnectionFactory;
import cn.ly.base_common.mq.activemq.registry.ActiveMQQueueConfigBeanRegistryConfiguration;

import com.timgroup.statsd.StatsDClient;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

import lombok.AllArgsConstructor;

/**
 * Created by liaomengge on 2019/5/5.
 */
@AllArgsConstructor
@Configuration
@ConditionalOnClass({ConnectionFactory.class, ActiveMQConnectionFactory.class})
@ConditionalOnProperty(name = "ly.mq.type", havingValue = "activemq")
@EnableConfigurationProperties(ActiveMQProperties.class)
@Import(ActiveMQQueueConfigBeanRegistryConfiguration.class)
public class ActiveMQAutoConfiguration {

    private final ActiveMQProperties activeMQProperties;

    @Bean(destroyMethod = "stop")
    @Primary
    @ConditionalOnMissingBean
    public PooledConnectionFactory pooledConnectionFactory() {
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(this.activeMQProperties.getBrokerUrl());
        PooledConnectionFactory pooledConnectionFactory = new MonitorPooledConnectionFactory(activeMQConnectionFactory);
        Pool pool = this.activeMQProperties.getPool();
        pooledConnectionFactory.setBlockIfSessionPoolIsFull(pool.isBlockIfFull());
        pooledConnectionFactory.setBlockIfSessionPoolIsFullTimeout(pool.getBlockIfFullTimeout());
        pooledConnectionFactory.setCreateConnectionOnStartup(pool.isCreateConnectionOnStartup());
        pooledConnectionFactory.setExpiryTimeout(pool.getExpiryTimeout());
        pooledConnectionFactory.setIdleTimeout(pool.getIdleTimeout());
        pooledConnectionFactory.setMaxConnections(pool.getMaxConnections());
        pooledConnectionFactory.setMaximumActiveSessionPerConnection(pool.getMaximumActiveSessionPerConnection());
        pooledConnectionFactory.setReconnectOnException(pool.isReconnectOnException());
        pooledConnectionFactory.setTimeBetweenExpirationCheckMillis(pool.getTimeBetweenExpirationCheck());
        pooledConnectionFactory.setUseAnonymousProducers(pool.isUseAnonymousProducers());
        return pooledConnectionFactory;
    }

    @Bean
    @ConditionalOnBean(StatsDClient.class)
    @ConditionalOnMissingBean
    public ActiveMQMonitor activeMQMonitor(StatsDClient statsDClient) {
        ActiveMQMonitor activeMQMonitor = new ActiveMQMonitor();
        activeMQMonitor.setStatsDClient(statsDClient);
        return activeMQMonitor;
    }
}
