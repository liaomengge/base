package com.github.liaomengge.base_common.nacos;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2020/8/17.
 */
@Data
@ConfigurationProperties("base.nacos")
public class NacosProperties {

    private RegistryProperties registry = new RegistryProperties();
    private ReceiveTrafficProperties receiveTraffic = new ReceiveTrafficProperties();

    @Data
    public class RegistryProperties {
        private boolean enabled = true;
    }

    @Data
    public class ReceiveTrafficProperties {
        private boolean enabled = false;
    }
}
