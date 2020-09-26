package cn.ly.base_common.metric.retrofit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Data
@ConfigurationProperties("ly.metric-retrofit")
public class MetricRetrofitProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
