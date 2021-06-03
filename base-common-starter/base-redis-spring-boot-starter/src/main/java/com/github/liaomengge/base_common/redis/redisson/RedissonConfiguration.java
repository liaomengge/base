package com.github.liaomengge.base_common.redis.redisson;

import com.github.liaomengge.base_common.helper.lock.distributed.redis.RedissonConfigManager;
import com.github.liaomengge.base_common.helper.lock.distributed.redis.RedissonLocker;
import com.github.liaomengge.base_common.helper.lock.distributed.redis.aspect.RedissonAspectLocker;
import com.github.liaomengge.base_common.helper.redis.RedissonHelper;
import com.github.liaomengge.base_common.redis.RedissonProperties;
import com.github.liaomengge.base_common.utils.io.LyIOUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "base.redis.redisson", name = "enabled")
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    @ConditionalOnMissingBean
    public RedissonAspectLocker redissonAspectLocker(RedissonLocker redissonLocker) {
        return new RedissonAspectLocker(redissonLocker);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonLocker redissonLocker(RedissonConfigManager redissonConfigManager) {
        return new RedissonLocker(redissonConfigManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonHelper redissonHelper(RedissonClient redissonClient) {
        return new RedissonHelper(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonConfigManager redissonConfigManager(RedissonClient redissonClient) {
        return new RedissonConfigManager(redissonClient);
    }

    @Bean(destroyMethod = "shutdown")
    @ConditionalOnMissingBean
    public RedissonClient redissonClient() {
        Config config;
        InputStream inputStream = null;
        try {
            inputStream = getConfigStream();
            ConfigSupport support = new ConfigSupport();
            config = support.fromJSON(inputStream, Config.class);
        } catch (IOException e) {
            try {
                inputStream = getConfigStream();
                config = Config.fromYAML(inputStream);
            } catch (IOException e1) {
                throw new IllegalArgumentException("Can't parse config", e1);
            }
        } finally {
            LyIOUtil.closeQuietly(inputStream);
        }

        return Redisson.create(config);
    }

    private InputStream getConfigStream() throws IOException {
        ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
        Resource resource = resourceResolver.getResource(redissonProperties.getConfigLocation());
        return resource.getInputStream();
    }
}
