package cn.ly.base_common.xxljob;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by liaomengge on 2020/8/11.
 */
@Data
@ConfigurationProperties("ly.xxl-job")
public class XxlJobProperties {

    private boolean enabled = true;
    private String accessToken;
    private AdminProperties admin = new AdminProperties();
    private ExecutorProperties executor = new ExecutorProperties();

    @Data
    public static class AdminProperties {
        private String addresses;
    }

    @Data
    public static class ExecutorProperties {
        private String logPath;
        private String ip;
        private Integer port = 9999;
        private Integer logRetentionDays = -1;
    }
}
