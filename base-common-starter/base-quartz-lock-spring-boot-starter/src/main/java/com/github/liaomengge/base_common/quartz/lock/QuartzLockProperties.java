package com.github.liaomengge.base_common.quartz.lock;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Created by liaomengge on 2019/5/21.
 */
@Data
@Validated
@ConfigurationProperties("base.quartz.lock")
public class QuartzLockProperties {

    private int lockNumber = 1;
    private String rootNode = "/base/quartz";
    private ZkProperties zk = new ZkProperties();

    @Data
    @Validated
    public static class ZkProperties {
        @NotNull
        private String zkServers;
        private int sessionTimeout = 30000;
        private int connectionTimeout = 5000;
    }
}
