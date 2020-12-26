package com.github.liaomengge.base_common.metric.cache.redis;

import com.github.liaomengge.base_common.redis.JedisClusterProperties;
import com.github.liaomengge.base_common.redis.RedisAutoConfiguration;
import com.github.liaomengge.base_common.redis.SpringDataProperties;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by liaomengge on 2020/9/22.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({MetricsAutoConfiguration.class, RedisAutoConfiguration.class})
@ConditionalOnClass({MeterRegistry.class, JedisPoolConfig.class})
@ConditionalOnProperty(prefix = "base.metric.cache.redis", name = "enabled", matchIfMissing = true)
public class RedisCacheMeterAutoConfiguration {

    private final JedisClusterProperties jedisClusterProperties;
    private final SpringDataProperties springDataProperties;

    public RedisCacheMeterAutoConfiguration(ObjectProvider<JedisClusterProperties> objectProvider,
                                            ObjectProvider<SpringDataProperties> objectProvider2) {
        this.jedisClusterProperties = objectProvider.getIfAvailable();
        this.springDataProperties = objectProvider2.getIfAvailable();
    }

    @Bean
    public RedisCacheMeterBinder redisCacheMeterBinder() {
        return new RedisCacheMeterBinder(jedisClusterProperties, springDataProperties);
    }
}
