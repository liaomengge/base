package com.github.liaomengge.base_common.redis;

import com.github.liaomengge.base_common.redis.jedis.JedisClusterConfiguration;
import com.github.liaomengge.base_common.redis.redisson.RedissonConfiguration;
import com.github.liaomengge.base_common.redis.spring.SpringDataConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Created by liaomengge on 2018/11/14.
 */
@Configuration
@Import({JedisClusterConfiguration.class, SpringDataConfiguration.class, RedissonConfiguration.class})
public class RedisAutoConfiguration {
}
