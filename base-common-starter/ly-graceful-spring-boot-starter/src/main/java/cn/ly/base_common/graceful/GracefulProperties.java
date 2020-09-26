package cn.ly.base_common.graceful;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Created by liaomengge on 2018/12/18.
 */
@Data
@ConfigurationProperties(prefix = "ly.graceful")
public class GracefulProperties {

    private int timeout = 30;//单位:秒
    private final Tomcat tomcat = new Tomcat();

    @Data
    public static class Tomcat {
        private boolean enabled;
    }
}
