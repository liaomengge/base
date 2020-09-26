package cn.ly.base_common.logger;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;

import lombok.Data;

/**
 * Created by liaomengge on 2019/1/21.
 */
@Data
@ConfigurationProperties(prefix = "ly.log")
public class LoggerProperties {

    private String contextPath = "/log";
    private String pkg = LoggingSystem.ROOT_LOGGER_NAME;
    private String level = LogLevel.INFO.name();
}
