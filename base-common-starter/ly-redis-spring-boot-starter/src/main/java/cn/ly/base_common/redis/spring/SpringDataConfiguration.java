package cn.ly.base_common.redis.spring;

import cn.ly.base_common.helper.redis.RedisTemplateHelper;
import cn.ly.base_common.redis.SpringDataProperties;
import lombok.NonNull;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.JedisClientConfigurationBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration.JedisClientConfigurationBuilder;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Configuration
@EnableConfigurationProperties(SpringDataProperties.class)
@ConditionalOnProperty(prefix = "ly.redis.spring-data", name = "enabled")
@ConditionalOnClass({GenericObjectPool.class, JedisConnection.class, Jedis.class})
public class SpringDataConfiguration {

    @Autowired
    private SpringDataProperties springDataProperties;

    @Bean(destroyMethod = "destroy")
    @ConditionalOnMissingBean(RedisConnectionFactory.class)
    public JedisConnectionFactory jedisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        return createJedisConnectionFactory(builderCustomizers);
    }

    private JedisConnectionFactory createJedisConnectionFactory(ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfiguration clientConfiguration = getJedisClientConfiguration(builderCustomizers);
        if (this.springDataProperties.getSentinelConfiguration() != null) {
            return new JedisConnectionFactory(this.springDataProperties.getSentinelConfiguration(),
                    clientConfiguration);
        }
        if (this.springDataProperties.getClusterConfiguration() != null) {
            return new JedisConnectionFactory(this.springDataProperties.getClusterConfiguration(), clientConfiguration);
        }
        return new JedisConnectionFactory(this.springDataProperties.getStandaloneConfig(), clientConfiguration);
    }

    private JedisClientConfiguration getJedisClientConfiguration(
            ObjectProvider<JedisClientConfigurationBuilderCustomizer> builderCustomizers) {
        JedisClientConfigurationBuilder builder = applyProperties(JedisClientConfiguration.builder());
        JedisPoolConfig jedisPoolConfig = springDataProperties.getPool();
        if (Objects.nonNull(jedisPoolConfig)) {
            builder.usePooling().poolConfig(jedisPoolConfig);
        }
        if (StringUtils.hasText(springDataProperties.getUrl())) {
            customizeConfigurationFromUrl(builder);
        }
        builderCustomizers.orderedStream().forEach((customizer) -> customizer.customize(builder));
        return builder.build();
    }

    private JedisClientConfigurationBuilder applyProperties(JedisClientConfigurationBuilder builder) {
        if (springDataProperties.isSsl()) {
            builder.useSsl();
        }
        if (springDataProperties.getTimeout() != null) {
            Duration timeout = springDataProperties.getTimeout();
            builder.readTimeout(timeout).connectTimeout(timeout);
        }
        if (StringUtils.hasText(springDataProperties.getClientName())) {
            builder.clientName(springDataProperties.getClientName());
        }
        return builder;
    }

    private void customizeConfigurationFromUrl(JedisClientConfigurationBuilder builder) {
        String url = springDataProperties.getUrl();

        if (url.startsWith("rediss://")) {
            builder.useSsl();
        }
    }

    @Bean
    @ConditionalOnMissingBean
    public StringRedisTemplate stringRedisTemplate(@NonNull RedisConnectionFactory redisConnectionFactory) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisConnectionFactory);
        return stringRedisTemplate;
    }

    @Bean
    @ConditionalOnMissingBean(name = "redisTemplate")
    public <K, V> RedisTemplate<K, V> redisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<K, V> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);
        return template;
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisTemplateHelper redisTemplateHelper(StringRedisTemplate stringRedisTemplate) {
        return new RedisTemplateHelper(stringRedisTemplate);
    }
}
