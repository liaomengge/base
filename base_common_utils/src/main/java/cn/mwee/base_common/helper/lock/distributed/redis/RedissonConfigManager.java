package cn.mwee.base_common.helper.lock.distributed.redis;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.InitializingBean;

import java.util.Objects;

/**
 * Created by liaomengge on 17/12/20.
 */
public class RedissonConfigManager implements InitializingBean {

    private static final int SCAN_INTERVAL = 3_000;//单位:ms
    public static final int CONNECT_TIMEOUT = 5_000;//单位:ms

    private String masterName;
    private String[] sentinelAddress;
    private String[] nodeAddress;

    @Getter
    private Config config;
    @Getter
    private RedissonClient redissonClient;

    public RedissonConfigManager(String masterName, String[] sentinelAddress) {
        this.masterName = masterName;
        this.sentinelAddress = sentinelAddress;
    }

    public RedissonConfigManager(String[] nodeAddress) {
        this.nodeAddress = nodeAddress;
    }

    public RedissonConfigManager(Config config) {
        this.config = config;
        redissonClient = Redisson.create(this.config);
    }

    public RedissonConfigManager(RedissonClient redissonClient) {
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

    @Override
    public void afterPropertiesSet() {
        if (redissonClient == null) {
            if (StringUtils.isNotBlank(masterName)) {
                redissonClient = getSentinelRedissonClient();
                return;
            }
            redissonClient = getNodeRedissonClient();
        }
    }

    /**
     * springboot直接管理redisson client shutdown,传统的spring xml需要手动设置destroy-method {@code destory}
     */
    public void destroy() {
        if (Objects.nonNull(redissonClient)) {
            redissonClient.shutdown();
        }
    }
}
