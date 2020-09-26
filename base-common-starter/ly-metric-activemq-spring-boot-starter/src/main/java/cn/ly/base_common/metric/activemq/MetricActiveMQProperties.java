package cn.ly.base_common.metric.activemq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Data
@ConfigurationProperties("ly.metric-activemq")
public class MetricActiveMQProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
