package cn.mwee.base_common.metric.redis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Data
@ConfigurationProperties("mwee.metric-redis")
public class MetricRedisProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
