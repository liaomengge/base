package cn.ly.base_common.graceful;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Data
@ConfigurationProperties(prefix = "mwee.graceful")
public class GracefulProperties {

    private int timeout = 30;//单位:秒
    private final Tomcat tomcat = new Tomcat();

    @Data
    public static class Tomcat {
        private boolean enabled;
    }
}
