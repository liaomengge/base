package cn.ly.base_common.logger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

/**
 * Created by liaomengge on 2019/1/21.
 */
@Data
@ConfigurationProperties(prefix = "ly.logger")
public class LoggerProperties {

    private String contextPath = "/logger";
    private String pkg = LoggingSystem.ROOT_LOGGER_NAME;
    private String level = LogLevel.INFO.name();
}