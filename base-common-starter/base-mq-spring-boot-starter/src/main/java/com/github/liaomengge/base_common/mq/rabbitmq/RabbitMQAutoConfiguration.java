package com.github.liaomengge.base_common.mq.rabbitmq;

import com.github.liaomengge.base_common.helper.mail.MailHelper;
import com.github.liaomengge.base_common.mq.rabbitmq.callback.MQConfirmCallback;
import com.github.liaomengge.base_common.mq.rabbitmq.callback.MQReturnCallback;
import com.github.liaomengge.base_common.mq.rabbitmq.monitor.DefaultMQMonitor;
import com.github.liaomengge.base_common.mq.rabbitmq.registry.RabbitMQQueueConfigBeanRegistryConfiguration;
import com.rabbitmq.client.Channel;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import java.util.Objects;

/**
 * Created by liaomengge on 2019/5/5.
 */
@AllArgsConstructor
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({RabbitTemplate.class, Channel.class})
@ConditionalOnProperty(name = "base.mq.type", havingValue = "rabbitmq", matchIfMissing = true)
@EnableConfigurationProperties(RabbitMQProperties.class)
@Import(RabbitMQQueueConfigBeanRegistryConfiguration.class)
public class RabbitMQAutoConfiguration {

    private final RabbitMQProperties rabbitMQProperties;

    @Bean(destroyMethod = "destroy")
    @Primary
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        if (StringUtils.isNotBlank(this.rabbitMQProperties.getVirtualHost())) {
            cachingConnectionFactory.setVirtualHost(this.rabbitMQProperties.getVirtualHost());
        }
        if (StringUtils.isNotBlank(this.rabbitMQProperties.getAddresses())) {
            cachingConnectionFactory.setAddresses(this.rabbitMQProperties.getAddresses());
        }
        if (StringUtils.isNotBlank(this.rabbitMQProperties.getUsername())) {
            cachingConnectionFactory.setUsername(this.rabbitMQProperties.getUsername());
        }
        if (StringUtils.isNotBlank(this.rabbitMQProperties.getPassword())) {
            cachingConnectionFactory.setPassword(this.rabbitMQProperties.getPassword());
        }
        if (Objects.nonNull(this.rabbitMQProperties.getRequestedHeartbeat())) {
            cachingConnectionFactory.setRequestedHeartBeat(this.rabbitMQProperties.getRequestedHeartbeat());
        }
        if (Objects.nonNull(this.rabbitMQProperties.getConnectionTimeout())) {
            cachingConnectionFactory.setConnectionTimeout(this.rabbitMQProperties.getConnectionTimeout());
        }
        cachingConnectionFactory.setPublisherConfirmType(this.rabbitMQProperties.getPublisherConfirmType());
        cachingConnectionFactory.setPublisherReturns(this.rabbitMQProperties.isPublisherReturns());

        if (Objects.nonNull(this.rabbitMQProperties.getCache().getChannel().getSize())) {
            cachingConnectionFactory.setChannelCacheSize(this.rabbitMQProperties.getCache().getChannel().getSize());
        }
        if (Objects.nonNull(this.rabbitMQProperties.getCache().getConnection().getMode())) {
            cachingConnectionFactory.setCacheMode(this.rabbitMQProperties.getCache().getConnection().getMode());
        }
        if (Objects.nonNull(this.rabbitMQProperties.getCache().getConnection().getSize())) {
            cachingConnectionFactory.setConnectionCacheSize(this.rabbitMQProperties.getCache().getConnection().getSize());
        }
        if (Objects.nonNull(this.rabbitMQProperties.getCache().getChannel().getCheckoutTimeout())) {
            cachingConnectionFactory.setChannelCheckoutTimeout(this.rabbitMQProperties.getCache().getChannel().getCheckoutTimeout());
        }
        return cachingConnectionFactory;
    }

    @Bean
    @ConditionalOnMissingBean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory cachingConnectionFactory) {
        return new RabbitAdmin(cachingConnectionFactory);
    }

    @Bean("com.github.liaomengge.base_common.mq.rabbitmq.monitor.DefaultMQMonitor")
    @ConditionalOnBean(MeterRegistry.class)
    @ConditionalOnMissingBean
    public DefaultMQMonitor rabbitMQMonitor(MeterRegistry meterRegistry) {
        DefaultMQMonitor mqMonitor = new DefaultMQMonitor();
        mqMonitor.setMeterRegistry(meterRegistry);
        return mqMonitor;
    }

    @Bean
    @ConditionalOnBean(MailHelper.class)
    @ConditionalOnMissingBean
    public MQConfirmCallback mqConfirmCallback(MailHelper mailHelper) {
        return new MQConfirmCallback(mailHelper);
    }

    @Bean
    @ConditionalOnMissingBean
    public MQReturnCallback mqReturnCallback() {
        return new MQReturnCallback();
    }

    @Bean
    @ConditionalOnMissingBean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();
        ExponentialBackOffPolicy backOffPolicy = new ExponentialBackOffPolicy();
        backOffPolicy.setInitialInterval(this.rabbitMQProperties.getRetry().getInitialInterval());
        backOffPolicy.setMultiplier(this.rabbitMQProperties.getRetry().getMultiplier());
        backOffPolicy.setMaxInterval(this.rabbitMQProperties.getRetry().getMaxInterval());
        retryTemplate.setBackOffPolicy(backOffPolicy);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(this.rabbitMQProperties.getRetry().getMaxAttempts());
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate;
    }
}
