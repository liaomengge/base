package com.github.liaomengge.base_common.framework;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/11/6.
 */
@Data
@ConfigurationProperties(prefix = "base.framework")
public class FrameworkProperties {

    private final SentinelProperties sentinel = new SentinelProperties();

    @Data
    public static class SentinelProperties {
        private boolean enabled;
    }
}
