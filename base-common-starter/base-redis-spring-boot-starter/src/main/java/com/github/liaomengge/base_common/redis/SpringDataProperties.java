package com.github.liaomengge.base_common.redis;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.connection.*;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by liaomengge on 2018/11/14.
 */
@Data
@ConfigurationProperties(prefix = "base.redis.spring-data")
public class SpringDataProperties {

    private boolean enabled;
    private int database = 0;
    private String url;
    private String host = "localhost";
    private String password;
    private int port = 6379;
    private boolean ssl = false;
    private Duration timeout = Duration.ofMillis(2000L);
    private String clientName;

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
            if (getPassword() != null) {
                config.setPassword(RedisPassword.of(getPassword()));
            }
            config.setDatabase(getDatabase());
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
                nodes.add(new RedisNode(parts[0], Integer.parseInt(parts[1])));
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
            if (getPassword() != null) {
                config.setPassword(RedisPassword.of(getPassword()));
            }
            return config;
        }
        return null;
    }

    public final RedisStandaloneConfiguration getStandaloneConfig() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        if (StringUtils.hasText(getUrl())) {
            ConnectionInfo connectionInfo = parseUrl(getUrl());
            config.setHostName(connectionInfo.getHostName());
            config.setPort(connectionInfo.getPort());
            config.setPassword(RedisPassword.of(connectionInfo.getPassword()));
        } else {
            config.setHostName(getHost());
            config.setPort(getPort());
            config.setPassword(RedisPassword.of(getPassword()));
        }
        config.setDatabase(getDatabase());
        return config;
    }

    protected ConnectionInfo parseUrl(String url) {
        try {
            URI uri = new URI(url);
            boolean useSsl = (url.startsWith("rediss://"));
            String password = null;
            if (uri.getUserInfo() != null) {
                password = uri.getUserInfo();
                int index = password.indexOf(':');
                if (index >= 0) {
                    password = password.substring(index + 1);
                }
            }
            return new ConnectionInfo(uri, useSsl, password);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Malformed url '" + url + "'", ex);
        }
    }

    @Getter
    @AllArgsConstructor
    protected static class ConnectionInfo {

        private final URI uri;
        private final boolean useSsl;
        private final String password;

        protected String getHostName() {
            return this.uri.getHost();
        }

        protected int getPort() {
            return this.uri.getPort();
        }
    }
}
