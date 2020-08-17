package cn.ly.base_common.eureka;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2020/8/15.
 */
@Data
@ConfigurationProperties("ly.eureka")
public class EurekaProperties {

    private Registry registry = new Registry();

    @Data
    public class Registry {
        private boolean enabled = true;
    }
}
