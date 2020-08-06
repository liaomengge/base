package cn.ly.base_common.redis.redisson;

import cn.ly.base_common.helper.lock.distributed.redis.RedisLocker;
import cn.ly.base_common.helper.lock.distributed.redis.RedissonConfigManager;
import cn.ly.base_common.helper.redis.RedissonHelper;
import cn.ly.base_common.redis.RedissonProperties;
import cn.ly.base_common.utils.io.LyIOUtil;
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
@Configuration
@ConditionalOnClass(Redisson.class)
@ConditionalOnProperty(prefix = "ly.redis.redisson", name = "enabled")
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonConfiguration {

    @Autowired
    private RedissonProperties redissonProperties;

    @Bean
    @ConditionalOnMissingBean
    public RedisLocker redisLocker(RedissonConfigManager redissonConfigManager) {
        return new RedisLocker(redissonConfigManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonConfigManager redissonConfigManager(RedissonClient redissonClient) {
        return new RedissonConfigManager(redissonClient);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedissonHelper redissonHelper(RedissonClient redissonClient) {
        return new RedissonHelper(redissonClient);
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
