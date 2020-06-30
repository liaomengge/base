package cn.ly.base_common.redis;

import cn.ly.base_common.redis.jedis.JedisClusterConfiguration;
import cn.ly.base_common.redis.redisson.RedissonConfiguration;
import cn.ly.base_common.redis.spring.SpringDataConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/11/14.
 */
@Configuration
@Import({JedisClusterConfiguration.class, SpringDataConfiguration.class, RedissonConfiguration.class})
public class RedisAutoConfiguration {
}
