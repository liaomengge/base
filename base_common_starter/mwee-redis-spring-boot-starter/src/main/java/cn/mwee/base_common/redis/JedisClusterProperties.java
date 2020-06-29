package cn.mwee.base_common.redis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import redis.clients.jedis.JedisPoolConfig;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by liaomengge on 2018/11/16.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "mwee.redis.jedis-cluster")
public class JedisClusterProperties {

    private boolean enabled;
    @NotNull
    private List<String> nodes;
    private int timeout = 2000;
    private int maxAttempts = 5;
    private JedisPoolWrapper pool = new JedisPoolWrapper();

    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class JedisPoolWrapper extends JedisPoolConfig {
        //如果要做jmx监控, 强烈建议设置jmxNamePrefix, 而不是使用默认的
    }
}
