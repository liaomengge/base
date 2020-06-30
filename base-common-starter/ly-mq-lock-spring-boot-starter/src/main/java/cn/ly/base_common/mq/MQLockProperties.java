package cn.ly.base_common.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by liaomengge on 2019/8/29.
 */
@Data
@Validated
@ConfigurationProperties(prefix = "mwee.mq.lock")
public class MQLockProperties {

    private int lockNumber = 1;
    private String rootNode = "/mwee/mq";
    private ZkProperties zk = new ZkProperties();
    private PrototypeProperties prototype = new PrototypeProperties();

    @Data
    @Validated
    public static class ZkProperties {
        @NotNull
        private String zkServers;
        private int sessionTimeout = 30000;
        private int connectionTimeout = 5000;
    }

    @Data
    public static class PrototypeProperties {
        private List<String> beanNames;
    }
}
