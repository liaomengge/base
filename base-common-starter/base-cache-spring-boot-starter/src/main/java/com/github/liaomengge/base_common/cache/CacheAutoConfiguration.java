package com.github.liaomengge.base_common.cache;

import com.github.benmanes.caffeine.cache.CaffeineSpec;
import com.github.liaomengge.base_common.cache.CacheProperties.*;
import com.github.liaomengge.base_common.cache.caffeine.CaffeineCacheManager;
import com.github.liaomengge.base_common.cache.channel.RedisChannel;
import com.github.liaomengge.base_common.cache.redis.RedisCache;
import com.github.liaomengge.base_common.cache.redis.RedissonClientManager;
import com.github.liaomengge.base_common.cache.servlet.CacheServlet;
import com.github.liaomengge.base_common.helper.redis.RedissonHelper;
import com.github.liaomengge.base_common.redis.RedisAutoConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by liaomengge on 2019/3/20.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(RedisAutoConfiguration.class)
@EnableConfigurationProperties(CacheProperties.class)
public class CacheAutoConfiguration {

    private final RedissonHelper redissonHelper;
    private final CacheProperties cacheProperties;

    public CacheAutoConfiguration(ObjectProvider<RedissonHelper> objectProvider, CacheProperties cacheProperties) {
        this.redissonHelper = objectProvider.getIfAvailable();
        this.cacheProperties = cacheProperties;
    }

    @RefreshScope
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager caffeineCacheManager = new CaffeineCacheManager();
        List<CaffeineSpecProperties> level1Properties = cacheProperties.getLevel1().getSpecProperties();
        Map<String, CaffeineSpec> caffeineSpecMap =
                level1Properties.stream().collect(Collectors.toMap(CaffeineSpecProperties::getRegion,
                        val -> CaffeineSpec.parse(val.getCaffeineSpec()), (oldValue, newValue) -> newValue));
        caffeineCacheManager.setCaffeineSpec(caffeineSpecMap);
        return caffeineCacheManager;
    }

    @Bean
    public RedisCache redisCache(@Qualifier("clusterRedissonClientManager") RedissonClientManager redissonClientManager) {
        Level2Properties level2Properties = this.cacheProperties.getLevel2();
        if (Objects.isNull(redissonHelper)) {
            return new RedisCache(level2Properties.isAllowNullValues(),
                    new RedissonHelper(redissonClientManager.getRedissonClient()));
        }
        return new RedisCache(level2Properties.isAllowNullValues(), redissonHelper);
    }

    @Bean(name = "clusterRedissonClientManager", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public RedissonClientManager clusterRedissonClientManager() {
        Level2Properties level2Properties = this.cacheProperties.getLevel2();
        String clusterConfigLocation = level2Properties.getClusterConfigLocation();
        if (StringUtils.isNotBlank(clusterConfigLocation)) {
            return new RedissonClientManager(clusterConfigLocation);
        }
        ClusterProperties clusterProperties = level2Properties.getCluster();
        return new RedissonClientManager(clusterProperties.getNodeAddress());
    }

    @Bean
    public RedisChannel channel(@Qualifier("sentinelRedissonClientManager") RedissonClientManager redissonClientManager) {
        return new RedisChannel(this.cacheProperties.getChannel().getChannelName(), redissonClientManager);
    }

    @Bean(name = "sentinelRedissonClientManager", destroyMethod = "destroy")
    @ConditionalOnMissingBean
    public RedissonClientManager sentinelRedissonClientManager() {
        ChannelProperties channelProperties = this.cacheProperties.getChannel();
        String sentinelConfigLocation = channelProperties.getSentinelConfigLocation();
        if (StringUtils.isNotBlank(sentinelConfigLocation)) {
            return new RedissonClientManager(sentinelConfigLocation);
        }
        SentinelProperties sentinelProperties = channelProperties.getSentinel();
        return new RedissonClientManager(sentinelProperties.getMasterName(), sentinelProperties.getSentinelAddress());
    }

    @Bean
    @ConditionalOnMissingBean
    public CachePoolHelper cachePoolHelper(CaffeineCacheManager caffeineCacheManager, RedisCache redisCache,
                                           RedisChannel channel) {
        return new CachePoolHelper(caffeineCacheManager, redisCache, channel);
    }

    @Bean
    @ConditionalOnBean(CachePoolHelper.class)
    @ConditionalOnMissingBean
    public LocalRedisDbCacheHelper cachePoolWrapperHelper(CachePoolHelper cachePoolHelper) {
        return new LocalRedisDbCacheHelper(cachePoolHelper);
    }

    @Bean
    public CacheServlet cacheServlet() {
        return new CacheServlet();
    }

    @Bean
    public ServletRegistrationBean cacheServletRegistrationBean(CacheServlet cacheServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean();
        registration.setServlet(cacheServlet);
        registration.addUrlMappings(cacheProperties.getContextPath());
        return registration;
    }
}
