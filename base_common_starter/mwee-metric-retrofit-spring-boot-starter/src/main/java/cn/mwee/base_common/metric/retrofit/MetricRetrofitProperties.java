package cn.mwee.base_common.metric.retrofit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/7/30.
 */
@Data
@ConfigurationProperties("mwee.metric-retrofit")
public class MetricRetrofitProperties {

    private boolean enabled;
    private long initialDelay = 120L;//单位：秒
    private long statsInterval = 20L;//单位：秒
}
