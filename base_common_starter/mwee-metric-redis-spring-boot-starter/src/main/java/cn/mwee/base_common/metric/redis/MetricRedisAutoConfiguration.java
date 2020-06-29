package cn.mwee.base_common.metric.redis;

import cn.mwee.base_common.metric.redis.task.MetricRedisScheduledTask;
import cn.mwee.base_common.redis.JedisClusterProperties;
import cn.mwee.base_common.redis.RedisAutoConfiguration;
import cn.mwee.base_common.redis.SpringDataProperties;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Configuration
@AutoConfigureAfter(RedisAutoConfiguration.class)
@ConditionalOnProperty(prefix = "mwee.metric-redis", name = "enabled", havingValue = "true")
@EnableConfigurationProperties(MetricRedisProperties.class)
public class MetricRedisAutoConfiguration {

    private final JedisClusterProperties jedisClusterProperties;
    private final SpringDataProperties springDataProperties;

    public MetricRedisAutoConfiguration(ObjectProvider<JedisClusterProperties> objectProvider,
                                        ObjectProvider<SpringDataProperties> objectProvider2) {
        this.jedisClusterProperties = objectProvider.getIfAvailable();
        this.springDataProperties = objectProvider2.getIfAvailable();
    }

    @Autowired
    private MetricRedisProperties metricRedisProperties;

    @Bean
    @ConditionalOnMissingBean
    public MetricRedisScheduledTask metricRedisScheduledTask(StatsDClient statsDClient) {
        return new MetricRedisScheduledTask(statsDClient, metricRedisProperties, jedisClusterProperties,
                springDataProperties);
    }
}
