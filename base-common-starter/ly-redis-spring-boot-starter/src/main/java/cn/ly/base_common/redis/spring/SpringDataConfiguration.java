package cn.ly.base_common.redis.spring;

import cn.ly.base_common.redis.SpringDataProperties;
import cn.ly.base_common.helper.redis.RedisTemplateHelper;
import lombok.NonNull;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
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
    public JedisConnectionFactory jedisConnectionFactory() {
        return applyProperties(createJedisConnectionFactory());
    }

    private JedisConnectionFactory applyProperties(JedisConnectionFactory jedisConnectionFactory) {
        configureConnection(jedisConnectionFactory);
        if (this.springDataProperties.isSsl()) {
            jedisConnectionFactory.setUseSsl(true);
        }
        jedisConnectionFactory.setDatabase(this.springDataProperties.getDatabase());
        long timeout = this.springDataProperties.getTimeout();
        if (timeout > 0) {
            jedisConnectionFactory.setTimeout((int) timeout);
        }
        return jedisConnectionFactory;
    }

    private void configureConnection(JedisConnectionFactory jedisConnectionFactory) {
        if (StringUtils.hasText(this.springDataProperties.getUrl())) {
            configureConnectionFromUrl(jedisConnectionFactory);
            return;
        }
        jedisConnectionFactory.setHostName(this.springDataProperties.getHost());
        jedisConnectionFactory.setPort(this.springDataProperties.getPort());
        if (this.springDataProperties.getPassword() != null) {
            jedisConnectionFactory.setPassword(this.springDataProperties.getPassword());
        }
    }

    private void configureConnectionFromUrl(JedisConnectionFactory factory) {
        String url = this.springDataProperties.getUrl();
        if (url.startsWith("rediss://")) {
            factory.setUseSsl(true);
        }
        try {
            URI uri = new URI(url);
            factory.setHostName(uri.getHost());
            factory.setPort(uri.getPort());
            if (uri.getUserInfo() != null) {
                String password = uri.getUserInfo();
                int index = password.indexOf(":");
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
                factory.setPassword(password);
            }
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed 'spring.redis.url' " + url, ex);
        }
    }

    private JedisConnectionFactory createJedisConnectionFactory() {
        JedisPoolConfig jedisPoolConfig = this.springDataProperties.getPool();
        if (Objects.isNull(jedisPoolConfig)) {
            jedisPoolConfig = new JedisPoolConfig();
        }
        if (this.springDataProperties.getSentinelConfiguration() != null) {
            return new JedisConnectionFactory(this.springDataProperties.getSentinelConfiguration(), jedisPoolConfig);
        }
        if (this.springDataProperties.getClusterConfiguration() != null) {
            return new JedisConnectionFactory(this.springDataProperties.getClusterConfiguration(), jedisPoolConfig);
        }
        return new JedisConnectionFactory(jedisPoolConfig);
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
