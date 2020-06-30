package cn.ly.base_common.metric.httpclient;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Data
@ConfigurationProperties("ly.metric-http-client")
public class MetricHttpClientProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
    private int maxHttpRoueCount = 5;//统计多少httpRoute
}
