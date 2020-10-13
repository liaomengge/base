package com.github.liaomengge.base_common.eureka;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Data
@ConfigurationProperties("base.eureka")
public class EurekaProperties {

    private Registry registry = new Registry();
    private Pull pull = new Pull();

    @Data
    public class Registry {
        private boolean enabled = true;
    }

    @Data
    public class Pull {
        private boolean enabled = false;
    }
}
