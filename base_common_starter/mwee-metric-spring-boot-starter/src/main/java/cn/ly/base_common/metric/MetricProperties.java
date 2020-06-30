package cn.ly.base_common.metric;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2018/12/19.
 */
@Data
@ConfigurationProperties("mwee.metric")
public class MetricProperties {

    private boolean enabled;
    private long initialDelay = 120L;
    private long period = 60L;
    private StatsdProperties statsd = new StatsdProperties();

    @Data
    public static class StatsdProperties {
        private String prefix;
        private String hostname;
        private int port;
    }
}
