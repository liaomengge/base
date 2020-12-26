package com.github.liaomengge.base_common.endpoint;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2019/7/4.
 */
@Data
@ConfigurationProperties("base.endpoint")
public class EndpointProperties {

    private InfoProperties info = new InfoProperties();

    @Data
    public static class InfoProperties {
        private boolean enabled = true;
    }
}
