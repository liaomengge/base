package cn.ly.base_common.metric.cache.redis;

import cn.ly.base_common.redis.JedisClusterProperties;
import cn.ly.base_common.redis.RedisAutoConfiguration;
import cn.ly.base_common.redis.SpringDataProperties;
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
@Configuration
@AutoConfigureAfter({MetricsAutoConfiguration.class, RedisAutoConfiguration.class})
@ConditionalOnClass({MeterRegistry.class, JedisPoolConfig.class})
@ConditionalOnProperty(prefix = "ly.metric.cache.redis", name = "enabled", matchIfMissing = true)
public class RedisCacheMetricsConfiguration {

    private final JedisClusterProperties jedisClusterProperties;
    private final SpringDataProperties springDataProperties;

    public RedisCacheMetricsConfiguration(ObjectProvider<JedisClusterProperties> objectProvider,
                                          ObjectProvider<SpringDataProperties> objectProvider2) {
        this.jedisClusterProperties = objectProvider.getIfAvailable();
        this.springDataProperties = objectProvider2.getIfAvailable();
    }

    @Bean
    public RedisCacheMetricsBinder redisCacheMetricsBinder() {
        return new RedisCacheMetricsBinder(jedisClusterProperties, springDataProperties);
    }
}
