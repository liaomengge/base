package cn.ly.base_common.cache;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2019/3/20.
 */
@Data
@ConfigurationProperties(prefix = "ly.cache")
public class CacheProperties {

    private Level1Properties level1 = new Level1Properties();
    private Level2Properties level2 = new Level2Properties();
    private ChannelProperties channel = new ChannelProperties();

    @Data
    public static class Level1Properties {
        private boolean allowNullValues;
        private List<CaffeineSpecProperties> specProperties;
    }

    @Data
    public static class CaffeineSpecProperties {
        private String region;
        private String caffeineSpec;
    }

    @Data
    public static class Level2Properties {
        private boolean allowNullValues;
        private String clusterConfigLocation;
        private ClusterProperties cluster = new ClusterProperties();
    }

    @Data
    public static class ClusterProperties {
        private String[] nodeAddress;
    }

    @Data
    public static class ChannelProperties {
        private String channelName;
        private String sentinelConfigLocation;
        private SentinelProperties sentinel = new SentinelProperties();
    }

    @Data
    public static class SentinelProperties {
        private String masterName;
        private String[] sentinelAddress;
    }

    protected String getContextPath() {
        return "/cache";
    }
}
