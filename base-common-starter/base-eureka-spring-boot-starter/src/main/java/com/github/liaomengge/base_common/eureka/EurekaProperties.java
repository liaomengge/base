package com.github.liaomengge.base_common.eureka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Data
@ConfigurationProperties("base.eureka")
public class EurekaProperties {

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
