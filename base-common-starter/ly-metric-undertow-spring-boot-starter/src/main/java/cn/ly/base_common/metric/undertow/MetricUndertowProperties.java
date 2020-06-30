package cn.ly.base_common.metric.undertow;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Data
@ConfigurationProperties("mwee.metric-undertow")
public class MetricUndertowProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
