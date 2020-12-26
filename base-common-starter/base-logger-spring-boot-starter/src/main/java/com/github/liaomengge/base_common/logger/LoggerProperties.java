package com.github.liaomengge.base_common.logger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * Created by liaomengge on 2019/1/21.
 */
@Data
@ConfigurationProperties("base.log")
public class LoggerProperties {

    private String contextPath = "/log";
    private Map<String, String> configureLevel;
}
