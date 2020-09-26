package cn.ly.base_common.cache.redis;


import cn.ly.base_common.utils.io.LyIOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.ConfigSupport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import lombok.Getter;

/**
 * Created by liaomengge on 17/12/20.
 */
public class RedissonClientManager implements InitializingBean {

    private static final int SCAN_INTERVAL = 3_000;//单位:ms
    private static final int CONNECT_TIMEOUT = 5_000;//单位:ms

    private String masterName;
    private String[] sentinelAddress;
    private String[] nodeAddress;
    private String configLocation;
    @Getter
    private RedissonClient redissonClient;

    public RedissonClientManager(String masterName, String[] sentinelAddress) {
        this.masterName = masterName;
        this.sentinelAddress = sentinelAddress;
    }

    public RedissonClientManager(String[] nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public RedissonClientManager(String configLocation) {
        this.configLocation = configLocation;
    }

    public RedissonClientManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    private RedissonClient getSentinelRedissonClient() {
        Config config = new Config();
        config.useSentinelServers().setMasterName(masterName).setConnectTimeout(CONNECT_TIMEOUT).addSentinelAddress(sentinelAddress);
        return Redisson.create(config);
    }

    private RedissonClient getNodeRedissonClient() {
        Config config = new Config();
        config.useClusterServers().setScanInterval(SCAN_INTERVAL).setConnectTimeout(CONNECT_TIMEOUT).addNodeAddress(nodeAddress);
        return Redisson.create(config);
    }

    private RedissonClient getConfigLocationRedissonClient() {
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
        Resource resource = resourceResolver.getResource(this.configLocation);
        return resource.getInputStream();
    }

    @Override
    public void afterPropertiesSet() {
        if (redissonClient == null) {
            if (StringUtils.isNotBlank(configLocation)) {
                redissonClient = getConfigLocationRedissonClient();
                return;
            }
            if (StringUtils.isNotBlank(masterName)) {
                redissonClient = getSentinelRedissonClient();
                return;
            }
            redissonClient = getNodeRedissonClient();
        }
    }

    public void destroy() {
        if (Objects.nonNull(redissonClient)) {
            redissonClient.shutdown();
        }
    }
}
