package cn.mwee.base_common.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/11/14.
 */
@Data
@ConfigurationProperties(prefix = "mwee.redis.spring-data")
public class SpringDataProperties {

    private boolean enabled;
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl = false;
    private long timeout = 2000L;

    private Sentinel sentinel;
    private Cluster cluster;
    private JedisPoolWrapper pool;

    @Data
    public static class Cluster {

        private List<String> nodes;
        private Integer maxRedirects;
    }

    @Data
    public static class Sentinel {

        private String master;
        private List<String> nodes;
    }

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class JedisPoolWrapper extends JedisPoolConfig {
        //如果要做jmx监控, 强烈建议设置jmxNamePrefix, 而不是使用默认的
    }

    public final RedisSentinelConfiguration getSentinelConfiguration() {
        Sentinel sentinelProperties = getSentinel();
        if (Objects.nonNull(sentinelProperties)) {
            RedisSentinelConfiguration config = new RedisSentinelConfiguration();
            config.master(sentinelProperties.getMaster());
            config.setSentinels(createSentinels(sentinelProperties));
            return config;
        }
        return null;
    }

    private List<RedisNode> createSentinels(Sentinel sentinel) {
        List<RedisNode> nodes = new ArrayList<>();
        for (String node : sentinel.getNodes()) {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                nodes.add(new RedisNode(parts[0], Integer.valueOf(parts[1])));
            } catch (RuntimeException ex) {
                throw new IllegalStateException(
                        "Invalid redis sentinel " + "property '" + node + "'", ex);
            }
        }
        return nodes;
    }

    public final RedisClusterConfiguration getClusterConfiguration() {
        Cluster cluster = getCluster();
        if (Objects.nonNull(cluster)) {
            RedisClusterConfiguration config = new RedisClusterConfiguration(cluster.getNodes());
            if (cluster.getMaxRedirects() != null) {
                config.setMaxRedirects(cluster.getMaxRedirects());
            }
            return config;
        }
        return null;
    }
}
